package com.bah.rtc.web;

import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.client.IProcessItemService;
import com.ibm.team.process.common.IProcessArea;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.process.common.ITeamArea;
import com.ibm.team.repository.client.ILoginHandler2;
import com.ibm.team.repository.client.ILoginInfo2;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.client.login.UsernameAndPasswordLoginInfo;
import com.ibm.team.repository.common.IContributor;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.IAuditableClient;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.common.model.IWorkItem;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamesflesher on 12/9/16.
 */
@Service
public class RTCServiceImpl implements RTCService {
    private ITeamRepository teamRepository;

    public void login(String url, String username, String password) throws AuthenticationException {
        if(!TeamPlatform.isStarted()) {
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
        if(TeamPlatform.isStarted() && teamRepository != null && teamRepository.loggedIn())
        {
            teamRepository.logout();
            TeamPlatform.shutdown();
        }
    }

    public WorkItem getWorkItem(String projectAreaName, String workItemId) {

        try {
            IProcessClientService processClient = (IProcessClientService) teamRepository
                    .getClientLibrary(IProcessClientService.class);
            IAuditableClient auditableClient = (IAuditableClient) teamRepository
                    .getClientLibrary(IAuditableClient.class);
            IWorkItemClient workItemClient = (IWorkItemClient) teamRepository
                    .getClientLibrary(IWorkItemClient.class);

            URI uri = URI.create(projectAreaName.replaceAll(" ", "%20"));
            IProjectArea projectArea = (IProjectArea) processClient
                    .findProcessArea(uri, null, null);
            if (projectArea == null) {
                System.out.println("Project area not found.");
                throw new RuntimeException("Project area not found.");
            }

            int id = new Integer(workItemId).intValue();

            IWorkItem workItem = workItemClient.findWorkItemById(id,
                    IWorkItem.FULL_PROFILE, null);

            WorkItem result = new WorkItem();
            result.setId(Long.valueOf(workItem.getId()));
            result.setTitle(workItem.getHTMLSummary().getPlainText());
            result.setDescription(workItem.getHTMLDescription().getPlainText());

            return result;
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Error getting work item.", ex);
        }
    }

    public List<ProjectArea> getProjectAreas() {
        List<ProjectArea> results = new ArrayList();

        try {
            IContributor contributor = teamRepository.loggedInContributor();
            IProcessItemService processItemService = (IProcessItemService) teamRepository.getClientLibrary(IProcessItemService.class);

            List<IProcessArea> processAreas = processItemService.findProcessAreas(contributor, null, IProcessClientService.ALL_PROPERTIES, null);

            for (IProcessArea processArea : processAreas) {
                if(processArea instanceof IProjectArea) {
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
}