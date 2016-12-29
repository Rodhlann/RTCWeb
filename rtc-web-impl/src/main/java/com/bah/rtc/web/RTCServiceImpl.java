package com.bah.rtc.web;

import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.client.IProcessItemService;
import com.ibm.team.process.common.IDevelopmentLine;
import com.ibm.team.process.common.IDevelopmentLineHandle;
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
import com.ibm.team.repository.common.query.IItemQuery;
import com.ibm.team.repository.common.query.IItemQueryPage;
import com.ibm.team.repository.common.query.ast.IItemQueryModel;
import com.ibm.team.repository.common.query.ast.IPredicate;
import com.ibm.team.repository.common.service.IQueryService;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.common.internal.model.query.BaseWorkItemQueryModel;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.IWorkItemHandle;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jamesflesher on 12/9/16.
 */
@Service
public class RTCServiceImpl implements RTCService {
    private ITeamRepository teamRepository;

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

    public boolean isAuthenticated() {
        return (TeamPlatform.isStarted() && teamRepository != null && teamRepository.loggedIn());
    }

    public static ILoginHandler2 getLoginHandler(final String user, final String password) {
        return new ILoginHandler2() {
            public ILoginInfo2 challenge(ITeamRepository repo) {
                return new UsernameAndPasswordLoginInfo(user, password);
            }
        };
    }

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

    public List<WorkItem> getWorkItems(String projectAreaName, String iterationName) {
        List<WorkItem> workItems = new ArrayList();

        try {
//            IProcessClientService processClient = (IProcessClientService) teamRepository
//                    .getClientLibrary(IProcessClientService.class);
//            IAuditableClient auditableClient = (IAuditableClient) teamRepository
//                    .getClientLibrary(IAuditableClient.class);
            IWorkItemClient workItemClient = (IWorkItemClient) teamRepository
                    .getClientLibrary(IWorkItemClient.class);

            BaseWorkItemQueryModel queryModel = BaseWorkItemQueryModel.WorkItemQueryModel.ROOT;
            IPredicate queryPredicate = queryModel.target().name()._eq(iterationName)
                    ._and(
                            queryModel.workItemType()._eq(WorkItemType.STORY.getTypeText())
                            ._or(queryModel.workItemType()._eq(WorkItemType.DEFECT.getTypeText()))
                            ._or(queryModel.workItemType()._eq(WorkItemType.EPIC.getTypeText()))
//                            ._or(queryModel.workItemType()._eq(WorkItemType.TASK.getTypeText()))
                    );

            IItemQuery query = IItemQuery.FACTORY.newInstance((IItemQueryModel)queryModel);
            query.filter(queryPredicate);

            IClientLibraryContext ctx = (IClientLibraryContext)teamRepository;
            IQueryService queryService = (IQueryService)ctx.getServiceInterface(IQueryService.class);

            IItemQueryPage qPage = queryService.queryItems(query, IQueryService.EMPTY_PARAMETERS, IQueryService.ITEM_QUERY_MAX_PAGE_SIZE);

//            HashMap<String, String> statuss = new HashMap<String, String>();

            if(qPage.getSize() > 0) {
                List<IWorkItemHandle> workItemHandles = new ArrayList();
                for(Object handle : qPage.getItemHandles()) {
                    workItemHandles.add((IWorkItemHandle)handle);
                }

                List<IWorkItem> workItems1 = teamRepository.itemManager().fetchCompleteItems(workItemHandles, IItemManager.DEFAULT, null);
                for(IWorkItem workItem : workItems1) {
                    WorkItem wi = new WorkItem();
                    wi.setId(Long.valueOf(workItem.getId()));
                    wi.setTitle(workItem.getHTMLSummary().getPlainText());
                    wi.setDescription(workItem.getHTMLDescription().getPlainText());
                    wi.setOwner(getUser(workItem.getOwner()));
                    wi.setStatus(getStatus(workItemClient, workItem));
                    wi.setType(WorkItemType.fromString(workItem.getWorkItemType()));
                    workItems.add(wi);

//                    if(!statuss.containsKey(wi.getStatus()))
//                    {
//                        System.out.println(wi.getStatus());
//                        statuss.put(wi.getStatus(), wi.getStatus());
//                    }
                }
            }

            return workItems;
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Error getting work item.", ex);
        }
    }

    private String getStatus(IWorkItemClient workItemClient, IWorkItem workItem) throws TeamRepositoryException {
        return workItemClient.findWorkflowInfo(workItem, null).getStateName(workItem.getState2());
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

    private List<Iteration> getIterations(List<IIterationHandle> iterationHandles) throws TeamRepositoryException {
        List<Iteration> plannedFor = new ArrayList();

        List<Iteration> iterationLines = teamRepository.itemManager().fetchCompleteItems(iterationHandles,IItemManager.DEFAULT, null);

        for(Iteration iteration : iterationLines) {
            if(iteration != null && iteration.getName() != null && !iteration.isArchived()) {
                plannedFor.add(iteration);

                if(iteration.getChildren() != null) {
                    plannedFor.addAll(getIterations(Arrays.asList(iteration.getChildren())));
                }
            }
        }

        return plannedFor;
    }
}