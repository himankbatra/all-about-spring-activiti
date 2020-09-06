package com.himank.activitiwithspring.servicetasks;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class SendEmailServiceTask implements JavaDelegate {

    public void execute(DelegateExecution execution) {
        //logic to sent email confirmation
        System.out.println("sent email To :-> "+ execution.getVariable("employeeName")+ "\n subject :-> "+
                " Vacation Leave Approved \n body :-> Hello "+ execution.getVariable("employeeName")
                +"!! Your "+ execution.getVariable("numberOfDays") + " days vacation leave is approved !!"
                + " Manager's Comment :-> "+execution.getVariable("comments"));
    }

}
