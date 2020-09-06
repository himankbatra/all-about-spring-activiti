package com.himank.activitiwithspring;


import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ProcessExecutionIntegrationTest {

    @Test
    public void givenBPMN_whenDeployProcess_thenDeployed() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.createDeployment()
                .addClasspathResource("org/activiti/test/vacationRequest.bpmn20.xml")
                .deploy();
        long count = repositoryService.createProcessDefinitionQuery().count();
        assertTrue(count >= 1);
    }

    @Test
    public void givenProcessDefinition_whenStartProcessInstance_thenProcessRunning() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.createDeployment()
                .addClasspathResource("org/activiti/test/vacationRequest.bpmn20.xml")
                .deploy();

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("employeeName", "Kermit");
        variables.put("numberOfDays", 4);
        variables.put("reason", "I'm really tired!");
        variables.put("startDate", LocalDate.parse("2020-09-26"));

        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("vacationRequest", variables);

        long count = runtimeService.createProcessInstanceQuery().count();
  /*     Task task = processEngine.getTaskService().createTaskQuery().singleResult();
        System.out.println("task = " + task.getDescription());*/


        TaskService taskService = processEngine.getTaskService();
        Task managementTask = taskService.createTaskQuery().taskCandidateGroup("management").singleResult();
        System.out.println("managementTask = " + managementTask.getDescription());

        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("vacationApproved", "false");
        taskVariables.put("comments", "We have a tight deadline!");
        taskService.complete(managementTask.getId(), taskVariables);

        Task currentTask = taskService.createTaskQuery().taskName("Modify vacation request").singleResult();
        System.out.println("currentTask = " + currentTask.getDescription());

        Map<String, Object> taskVars = new HashMap<String, Object>();
        taskVars.put("numberOfDays", 2);
        taskVars.put("reason", "i am tired enough !! please grant me 2 days leave");
        taskVars.put("startDate", LocalDate.parse("2020-09-26"));
        taskVars.put("resendRequest", "true");
        taskService.complete(currentTask.getId(), taskVars);

        Task resend_management = taskService.createTaskQuery().taskCandidateGroup("management").singleResult();
        System.out.println("resend_management task = " + resend_management.getDescription());

        Map<String, Object> taskVariables1 = new HashMap<String, Object>();
        taskVariables1.put("vacationApproved", "true");
        taskVariables1.put("comments", "approved 2 days leave ");
        taskService.complete(resend_management.getId(), taskVariables1);

        long count1 = runtimeService.createProcessInstanceQuery().count();

     /*   Task newCurrentTask = taskService.createTaskQuery().taskName("Send email confirmation").singleResult();
        System.out.println("newCurrentTask.getDescription() = " + newCurrentTask.getDescription());*/

        assertTrue(count >= 1);
        assertEquals(0, count1);
    }

    @Test
    public void givenProcessDefinition_whenStartProcessInstance_thenProcessRunningNew() {

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.createDeployment()
                .addClasspathResource("org/activiti/test/vacationRequestNew.bpmn20.xml")
                .deploy();

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("employeeName", "Kermit");
        variables.put("numberOfDays", 4);
        variables.put("vacationMotivation", "I'm really tired!");
        variables.put("startDate", LocalDate.parse("2020-09-26"));

        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("vacationRequest", variables);

        long count = runtimeService.createProcessInstanceQuery().count();
  /*     Task task = processEngine.getTaskService().createTaskQuery().singleResult();
        System.out.println("task = " + task.getDescription());*/


        TaskService taskService = processEngine.getTaskService();
        Task managementTask = taskService.createTaskQuery().taskCandidateGroup("management").singleResult();
        System.out.println("managementTask = " + managementTask.getDescription());

        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("vacationApproved", "false");
        taskVariables.put("managerMotivation", "We have a tight deadline!");
        taskService.complete(managementTask.getId(), taskVariables);

        Task currentTask = taskService.createTaskQuery().taskCandidateOrAssigned("Kermit").singleResult();
        System.out.println("currentTask = " + currentTask.getDescription());

        Map<String, Object> taskVars = new HashMap<String, Object>();
        taskVars.put("numberOfDays", 2);
        taskVars.put("vacationMotivation", "i am tired enough !! please grant me 2 days leave");
        taskVars.put("startDate", LocalDate.parse("2020-09-26"));
        taskVars.put("resendRequest", "true");
        taskService.complete(currentTask.getId(), taskVars);

        Task resend_management = taskService.createTaskQuery().taskCandidateGroup("management").singleResult();
        System.out.println("resend_management task = " + resend_management.getDescription());

        Map<String, Object> taskVariables1 = new HashMap<String, Object>();
        taskVariables1.put("vacationApproved", "true");
        taskVariables1.put("comments", "approved 2 days leave ");
        taskService.complete(resend_management.getId(), taskVariables1);

        long count1 = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstance.getProcessInstanceId()).count();

     /*   Task newCurrentTask = taskService.createTaskQuery().taskName("Send email confirmation").singleResult();
        System.out.println("newCurrentTask.getDescription() = " + newCurrentTask.getDescription());*/

        processEngine.getHistoryService().createHistoricTaskInstanceQuery().finished()
                .orderByHistoricTaskInstanceDuration().desc()
                .list().forEach(
                i -> System.out.println("completed task :-> " + i.getDescription()));
        assertTrue(count >= 1);
        assertEquals(0, count1);
    }


    @Test
    public void givenProcessDefinition_whenStartProcessInstance_thenProcessRunningFinancial() {

        // Create Activiti process engine
       /* ProcessEngine processEngine = ProcessEngineConfiguration
                .createStandaloneProcessEngineConfiguration()
                .buildProcessEngine();*/

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();


        // Get Activiti services
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        // Deploy the process definition
        repositoryService.createDeployment()
                .addClasspathResource("org/activiti/test/FinancialReportProcess.bpmn20.xml")
                .deploy();

        // Start a process instance
        String procId = runtimeService.startProcessInstanceByKey("financialReport").getId();

        // Get the first task
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("accountancy").list();
        for (Task task : tasks) {
            System.out.println("Following task is available for accountancy group: " + task.getName());

            // claim it
            taskService.claim(task.getId(), "fozzie");
        }

        // Verify Fozzie can now retrieve the task
        tasks = taskService.createTaskQuery().taskAssignee("fozzie").list();
        for (Task task : tasks) {
            System.out.println("Task for fozzie: " + task.getName());

            // Complete the task
            taskService.complete(task.getId());
        }

        System.out.println("Number of tasks for fozzie: "
                + taskService.createTaskQuery().taskAssignee("fozzie").count());

        // Retrieve and claim the second task
        tasks = taskService.createTaskQuery().taskCandidateGroup("management").list();
        for (Task task : tasks) {
            System.out.println("Following task is available for management group: " + task.getName());
            taskService.claim(task.getId(), "kermit");
        }

        // Completing the second task ends the process
        for (Task task : tasks) {
            System.out.println("Task for kermit: " + task.getName());
            taskService.complete(task.getId());
        }

        // verify that the process is actually finished
        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(procId).singleResult();
        System.out.println("Process instance end time: " + historicProcessInstance.getEndTime());


    }


    @Test
    public void givenProcessInstance_whenCompleteTask_thenProcessExecutionContinues() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.createDeployment()
                .addClasspathResource("org/activiti/test/vacationRequest.bpmn20.xml")
                .deploy();

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("employeeName", "Kermit");
        variables.put("numberOfDays", 4);
        variables.put("reason", "I'm really tired!");
        variables.put("startDate", LocalDate.parse("2020-09-26"));

        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("vacationRequest", variables);

        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("management").list();

        Task task = tasks.get(0);

        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("vacationApproved", "false");
        taskVariables.put("comments", "We have a tight deadline!");
        taskService.complete(task.getId(), taskVariables);

        Task currentTask = taskService.createTaskQuery().taskName("Modify vacation request").singleResult();
        assertNotNull(currentTask);
    }

    @Test(expected = ActivitiException.class)
    public void givenProcessDefinition_whenSuspend_thenNoProcessInstanceCreated() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.createDeployment()
                .addClasspathResource("org/activiti/test/vacationRequest.bpmn20.xml")
                .deploy();

        RuntimeService runtimeService = processEngine.getRuntimeService();
        repositoryService.suspendProcessDefinitionByKey("vacationRequest");
        runtimeService.startProcessInstanceByKey("vacationRequest");

    }
}
