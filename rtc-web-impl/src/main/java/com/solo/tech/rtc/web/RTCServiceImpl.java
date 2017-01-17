package com.solo.tech.rtc.web;

import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.client.IProcessItemService;
import com.ibm.team.process.common.IDevelopmentLine;
import com.ibm.team.process.common.IDevelopmentLineHandle;
import com.ibm.team.process.common.IIteration;
import com.ibm.team.process.common.IIterationHandle;
import com.ibm.team.process.common.IProcessArea;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.process.common.ITeamArea;
import com.ibm.team.process.internal.common.Iteration;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ILoginHandler2;
import com.ibm.team.repository.client.ILoginInfo2;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.client.internal.ItemManager;
import com.ibm.team.repository.client.login.UsernameAndPasswordLoginInfo;
import com.ibm.team.repository.client.util.IClientLibraryContext;
import com.ibm.team.repository.common.IContributor;
import com.ibm.team.repository.common.IContributorHandle;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.repository.common.internal.util.StringUtils;
import com.ibm.team.repository.common.query.IItemQuery;
import com.ibm.team.repository.common.query.IItemQueryPage;
import com.ibm.team.repository.common.query.ast.IItemQueryModel;
import com.ibm.team.repository.common.query.ast.IPredicate;
import com.ibm.team.repository.common.service.IQueryService;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.common.internal.model.query.BaseWorkItemQueryModel;
import com.ibm.team.workitem.common.model.ICategory;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.IWorkItemHandle;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.naming.AuthenticationException;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jamesflesher on 12/9/16.
 */
@Service
public class RTCServiceImpl implements RTCService {
    private ITeamRepository teamRepository;

    /**
     * Authenticate with the RTC server specified by the {@code url}
     * @param url
     *      {@link String} which represents the RTC Server HTTP endpoint in which the application will connect
     * @param username
     *      {@link String} of the user to authenticate
     * @param password
     *      {@link String} of the credentials to use to authenticate
     * @throws AuthenticationException
     */
    public void login(String url, String username, String password) throws AuthenticationException {
        if(!isAuthenticated()) {
            TeamPlatform.startup();
        }

        IProgressMonitor progressMonitor = new NullProgressMonitor();

        teamRepository = TeamPlatform.getTeamRepositoryService().getTeamRepository(url);
        teamRepository.registerLoginHandler(getLoginHandler(username, password));

        try {
            teamRepository.login(progressMonitor);
        } catch (TeamRepositoryException e) {
            throw new AuthenticationException();
        }
    }

    /**
     * Validates current credentials authenticated state;  Is used by the web application to detect if the session has
     * timed out and the user is no longer authenticated; if so they are re-directed to the login page
     * @return
     */
    public boolean isAuthenticated() {
        return (TeamPlatform.isStarted() && teamRepository != null && teamRepository.loggedIn());
    }

    /**
     * Implementation of {@link ILoginHandler2} to retrieve credentials for the {@link ITeamRepository}
     * @param user
     * @param password
     * @return
     */
    public static ILoginHandler2 getLoginHandler(final String user, final String password) {
        return new ILoginHandler2() {
            public ILoginInfo2 challenge(ITeamRepository repo) {
                return new UsernameAndPasswordLoginInfo(user, password);
            }
        };
    }

    /**
     * Invalidate the current credentials and disconnect from RTC Server
     */
    public void logout() {
        if(isAuthenticated())
        {
            teamRepository.logout();
            TeamPlatform.shutdown();
        }
    }

    private IProjectArea getProjectArea(String projectAreaName) throws TeamRepositoryException {
        IProcessClientService processClient = (IProcessClientService) teamRepository.getClientLibrary(IProcessClientService.class);
        URI uri = URI.create(projectAreaName.replaceAll(" ", "%20"));
        return (IProjectArea) processClient.findProcessArea(uri, null, null);
    }

    public List<Category> getCategories(String projectAreaName) {
        List<Category> categories = new ArrayList();

        try {
            IWorkItemClient workItemClient = (IWorkItemClient) teamRepository
                    .getClientLibrary(IWorkItemClient.class);
            IProjectArea projectArea = getProjectArea(projectAreaName);

            List<ICategory> projectCategories = workItemClient.findCategories(projectArea, ICategory.FULL_PROFILE, null);

            for(ICategory projectCategory : projectCategories) {
                Category category = new Category();
                category.setId(projectCategory.getItemId().getUuidValue());
                category.setName(projectCategory.getName());
                categories.add(category);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error getting categories.", ex);
        }
        return categories;
    }

    public List<WorkItem> getWorkItems(String projectAreaName, String iterationName, String teamName, List<String> tags) {
        List<WorkItem> workItems = new ArrayList();
        String[] selectedIterations = new String[] {};
        try {
//            IProcessClientService processClient = (IProcessClientService) teamRepository
//                    .getClientLibrary(IProcessClientService.class);
//            IAuditableClient auditableClient = (IAuditableClient) teamRepository
//                    .getClientLibrary(IAuditableClient.class);
            IWorkItemClient workItemClient = (IWorkItemClient) teamRepository
                    .getClientLibrary(IWorkItemClient.class);

            if(iterationName.equalsIgnoreCase("current")) {
                List<String> currentIterationNames = getCurrentIterations(projectAreaName)
                        .stream()
                        .map(IIteration::getName)
                        .collect(Collectors.toList());

                selectedIterations = new String[currentIterationNames.size()];
                currentIterationNames.toArray(selectedIterations);
            } else {
                selectedIterations = new String[] { iterationName };
            }

            BaseWorkItemQueryModel queryModel = BaseWorkItemQueryModel.WorkItemQueryModel.ROOT;
            IPredicate queryPredicate = queryModel.target().name()._in(selectedIterations)
                    ._and(
                            queryModel.workItemType()._eq(WorkItemType.STORY.getTypeText())
                            ._or(queryModel.workItemType()._eq(WorkItemType.DEFECT.getTypeText()))
                            ._or(queryModel.workItemType()._eq(WorkItemType.EPIC.getTypeText()))
//                            ._or(queryModel.workItemType()._eq(WorkItemType.TASK.getTypeText()))
                    );

            if(!StringUtils.isEmpty(teamName) && !teamName.equalsIgnoreCase("null") && !teamName.equalsIgnoreCase("none")) {
                queryPredicate = queryPredicate._and(
                        queryModel.category().name()._like(teamName)
                );
            }

            if(!CollectionUtils.isEmpty(tags)) {
                queryPredicate._and(
                        queryModel.internalTags()._in((String[]) tags.toArray())
                );
            }

            IItemQuery query = IItemQuery.FACTORY.newInstance((IItemQueryModel)queryModel);
            query.filter(queryPredicate);

            IClientLibraryContext ctx = (IClientLibraryContext)teamRepository;
            IQueryService queryService = (IQueryService)ctx.getServiceInterface(IQueryService.class);

            IItemQueryPage qPage = queryService.queryItems(query, IQueryService.EMPTY_PARAMETERS, IQueryService.ITEM_QUERY_MAX_PAGE_SIZE);

            HashMap<String, String> statusValues = new HashMap();
            HashMap<String, String> typeValues = new HashMap();

            while(qPage.getSize() > 0) {
                List<IWorkItemHandle> workItemHandles = new ArrayList();

                for (Object handle : qPage.getItemHandles()) {
                    workItemHandles.add((IWorkItemHandle) handle);
                }

                List<IWorkItem> workItems1 = teamRepository.itemManager().fetchCompleteItems(workItemHandles, IItemManager.DEFAULT, null);
                for (IWorkItem workItem : workItems1) {
                    WorkItem wi = new WorkItem();
                    wi.setId(Long.valueOf(workItem.getId()));
                    wi.setTitle(workItem.getHTMLSummary().getPlainText());
                    wi.setDescription(workItem.getHTMLDescription().getPlainText());
                    wi.setOwner(getUser(workItem.getOwner()));

                    String statusText = workItemClient.findWorkflowInfo(workItem, null).getStateName(workItem.getState2());
                    if(!statusValues.containsKey(statusText))
                    {
                        statusValues.put(statusText, statusText);
                    }

                    wi.setStatus(getStatus(workItemClient, workItem));

                    if(!typeValues.containsKey(workItem.getWorkItemType()))
                    {
                        typeValues.put(workItem.getWorkItemType(), workItem.getWorkItemType());
                    }

                    wi.setType(WorkItemType.fromString(workItem.getWorkItemType()));
                    wi.setFiledAgainst(getFiledAgainst(workItem).getName());
                    workItems.add(wi);
                }

                if(qPage.hasNext()) {
                    qPage = (IItemQueryPage) queryService.fetchPage(qPage.getToken(), qPage.getNextStartPosition(), IQueryService.ITEM_QUERY_MAX_PAGE_SIZE);
                } else {
                    break;
                }
            }

            System.out.println("STATUS VALUES");
            statusValues.keySet().stream().forEach(s -> { System.out.println(s); });

            System.out.println("TYPE VALUES");
            typeValues.keySet().stream().forEach(t -> { System.out.println(t); });

            return workItems;
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Error getting work item.", ex);
        }
    }

    private ICategory getFiledAgainst(IWorkItem workItem) throws TeamRepositoryException {
        return (ICategory)teamRepository.itemManager().fetchCompleteItem(workItem.getCategory(), IItemManager.DEFAULT, null);
    }

    private WorkItemStatus getStatus(IWorkItemClient workItemClient, IWorkItem workItem) throws TeamRepositoryException {

        WorkItemStatus resolvedStatus;
        String statusText = workItemClient.findWorkflowInfo(workItem, null).getStateName(workItem.getState2());

        switch(statusText) {
            case "Waiting for Information":
            case "New":
            case "Not Started":
            case "Not Done":
            case "Ready for Sizing":
            case "Ready for Dev":
                resolvedStatus = WorkItemStatus.READY_FOR_DEVELOPMENT;
                break;

            case "In Development":
            case "In Progress":
            case "Dev – Needs More Information":
            case "Needs More Information":
            case "Awaiting 3rd Party":
                resolvedStatus = WorkItemStatus.IN_PROGRESS;
                break;

            case "In Scrum Test":
            case "In Scrum Testing":
            case "In Retest":
                resolvedStatus = WorkItemStatus.IN_SCRUM_TEST;
                break;

            case "Done":
            case "Delivered":
            case "InDeployment":
            case "Closed":
            case "In Validation":
            case "Resolved":
                resolvedStatus = WorkItemStatus.DONE;
                break;

            default:
                resolvedStatus = WorkItemStatus.UNKNOWN;
        }

        return resolvedStatus;
    }

    private User getUser(IContributorHandle contributorHandle) throws TeamRepositoryException {
        User user = new User();

        IContributor contributor = (IContributor)teamRepository.itemManager().fetchCompleteItem(contributorHandle, ItemManager.DEFAULT, null);
        user.setId(contributor.getItemId().getUuidValue());
        user.setName(contributor.getName());
        user.setEmailAddress(contributor.getEmailAddress());
        user.setUserId(contributor.getUserId());

        return user;
    }

    public List<ProjectArea> getProjectAreas() {
        List<ProjectArea> results = new ArrayList();

        try {
            IContributor contributor = teamRepository.loggedInContributor();
            IProcessItemService processItemService = (IProcessItemService) teamRepository.getClientLibrary(IProcessItemService.class);

            List<IProcessArea> processAreas = processItemService.findProcessAreas(contributor, null, IProcessClientService.ALL_PROPERTIES, null);

            for (IProcessArea processArea : processAreas) {
                if(processArea instanceof IProjectArea) {
                    ((com.ibm.team.process.internal.common.ProjectArea)processArea).getDevelopmentLines();
                    ProjectArea projectArea = new ProjectArea();
                    projectArea.setName(processArea.getName());
                    projectArea.setId(processArea.getItemId().getUuidValue());
                    results.add(projectArea);
                }
            }
        } catch (Exception ex) {
          throw new RuntimeException("Error getting project areas");
        }

        return results;
    }

    public List<TeamArea> getTeamAreas() {
        List<TeamArea> teams = new ArrayList();

        try {
            IContributor contributor = teamRepository.loggedInContributor();
            IProcessItemService processItemService = (IProcessItemService) teamRepository.getClientLibrary(IProcessItemService.class);

            List<IProcessArea> processAreas = processItemService.findProcessAreas(contributor, null, IProcessClientService.ALL_PROPERTIES, null);

            for (IProcessArea processArea : processAreas) {
                if(processArea instanceof ITeamArea) {
                    TeamArea teamArea = new TeamArea();
                    teamArea.setName(processArea.getName());
                    teamArea.setId(processArea.getItemId().getUuidValue());
                    teams.add(teamArea);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error getting team areas");
        }

        return teams;
    }

    public List<Sprint> getSprints(String projectAreaName) {
        List<Sprint> sprints = new ArrayList();

        try {
            IProcessItemService processItemService = (IProcessItemService) teamRepository.getClientLibrary(IProcessItemService.class);

            IProjectArea projectArea = getProjectArea(projectAreaName);

            if(projectArea != null) {
                IDevelopmentLineHandle[] developmentHandles = projectArea.getDevelopmentLines();
                List<IDevelopmentLine> developmentLines = teamRepository.itemManager().fetchCompleteItems(Arrays.asList(developmentHandles), IItemManager.DEFAULT, null);

                for (IDevelopmentLine developmentLine : developmentLines) {
                    if(!developmentLine.isArchived()) {
                        List<IIterationHandle> iterationHandles = Arrays.asList(developmentLine.getIterations());
                        sprints.addAll(getPlannedForFromIterations(iterationHandles));
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error getting project area sprints");
        }

        return sprints;
    }

    private List<Sprint> getPlannedForFromIterations(List<IIterationHandle> iterationHandles) throws TeamRepositoryException {
        List<Sprint> plannedFor = new ArrayList();

        List<Iteration> iterationLines = teamRepository.itemManager().fetchCompleteItems(iterationHandles,IItemManager.DEFAULT, null);

        for(Iteration iteration : iterationLines) {
            if(iteration != null && iteration.getName() != null && !iteration.isArchived()) {
                Sprint sprint = new Sprint();
                sprint.setName(iteration.getName());
                sprint.setStartDate(iteration.getStartDate());
                sprint.setEndDate(iteration.getEndDate());
                plannedFor.add(sprint);

                if(iteration.getChildren() != null) {
                    plannedFor.addAll(getPlannedForFromIterations(Arrays.asList(iteration.getChildren())));
                }
            }
        }

        return plannedFor;
    }

    private List<Iteration> getCurrentIterations(String projectAreaName) throws TeamRepositoryException {
        List<Iteration> currentIterations = new ArrayList();

        IProjectArea projectArea = getProjectArea(projectAreaName);
        if(projectArea != null) {
            IDevelopmentLineHandle[] developmentLineHandles = projectArea.getDevelopmentLines();
            List<IDevelopmentLine> developmentLines = teamRepository.itemManager().fetchCompleteItems(Arrays.asList(developmentLineHandles), IItemManager.DEFAULT, null);

            List<IIterationHandle> developmentLineIterationHandles = new ArrayList();

            developmentLines.stream()
                .filter(iDevelopmentLine -> (!iDevelopmentLine.isArchived() && iDevelopmentLine.getIterations().length > 0))
                .forEach(iDevelopmentLine -> developmentLineIterationHandles.addAll(Arrays.asList(iDevelopmentLine.getIterations())));

            currentIterations = getCurrentIterations(developmentLineIterationHandles);
        }

        return currentIterations;
    }

    private List<Iteration> getCurrentIterations(List<IIterationHandle> iterationHandles) {
        List<Iteration> allIterations = new ArrayList();

        try {
            List<Iteration> iterations = teamRepository.itemManager().fetchCompleteItems(iterationHandles, IItemManager.DEFAULT, null);

            iterations.stream()
                    .filter(iIteration -> {
                        return (!StringUtils.isEmpty(iIteration.getName()) && !iIteration.isArchived() && (iIteration.getStartDate() == null ? false : iIteration.getStartDate().before(new Date())) && (iIteration.getEndDate() == null ? false : iIteration.getEndDate().after(new Date())));
                    })
                    .forEach(iteration -> {
                        allIterations.add(iteration);
                        allIterations.addAll(getCurrentIterations(Arrays.asList(iteration.getChildren())));
                    });
        } catch (Exception ex) {
            System.out.println("Unable to retrieve current iterations.");
        }

        return allIterations;
    }
}