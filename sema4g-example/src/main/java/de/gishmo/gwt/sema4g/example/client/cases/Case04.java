/*
 * Copyright (c) 2017 - 2018 - Frank Hossfeld
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.gishmo.gwt.sema4g.example.client.cases;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.github.mvp4g.sema4g.client.SeMa4g;
import com.github.mvp4g.sema4g.client.command.SeMa4gCommand;
import com.github.mvp4g.sema4g.client.exception.SeMa4gException;
import de.gishmo.gwt.sema4g.example.client.cases.code.Example;

@Example
public class Case04
  extends AbstractCase {

  public Case04(FlowPanel fp,
                PopupPanel popup) {
    super(fp,
          popup);

    descriptionText = "Several service calls with different duration on the server. With cycle dependencies, exception expected. No service will be called." +
                      "<ul>" +
                      "<li>service 'one': the waiting duration on the server is: 9250 ms.</li>" +
                      "<li>service 'two': the waiting duration on the server is: 3255 ms. The service depends on the execution of service 'six'.</li>" +
                      "<li>service 'three': the waiting duration on the server is: 125 ms. The service depends on the execution of service 'eight' and 'five'.</li>" +
                      "<li>service 'four': the waiting duration on the server is: 52000 ms. The service depends on the execution of service 'two'.</li>" +
                      "<li>service 'five': the waiting duration on the server is: 250 ms.</li>" +
                      "<li>service 'six': the waiting duration on the server is: 6000 ms. The service depends on the execution of service 'four', 'seven' and 'nine'.<br><b>This command has a cycle dependency!</b></li>" +
                      "<li>service 'seven': the waiting duration on the server is: 7250 ms.</li>" +
                      "<li>service 'eight': the waiting duration on the server is: 2400 ms.</li>" +
                      "<li>service 'nine': the waiting duration on the server is: 5100 ms.</li>" +
                      "<li>service 'ten': the waiting duration on the server is: 200 ms.</li>" +
                      "</ul>" +
                      "The context will not be executed because aof a cycle dependency!" +
                      AbstractCase.SERVICE_DESCRIPTION +
                      AbstractCase.INITCOMMAND_DESCRIPTION +
                      AbstractCase.FINALCOMMAND_DESCRIPTION;
    labelText = "Test Case 04";
    startText = "Execution for test case four started";
    successText = "Execution for case four finished";
    errorText = "Execution has detected a cycle dependency";
  }

  public void createContextAndRun() {
    SeMa4gCommand command01 = this.createAsyncCommandRPC(9250,
                                                         "one");
    SeMa4gCommand command02 = this.createAsyncCommandRPC(3255,
                                                         "two");
    SeMa4gCommand command03 = this.createAsyncCommandRPC(125,
                                                         "three");
    SeMa4gCommand command04 = this.createAsyncCommandRPC(5200,
                                                         "four");
    SeMa4gCommand command05 = this.createAsyncCommandRPC(250,
                                                         "five");
    SeMa4gCommand command06 = this.createAsyncCommandRPC(6000,
                                                         "six");
    SeMa4gCommand command07 = this.createAsyncCommandRPC(7250,
                                                         "seven");
    SeMa4gCommand command08 = this.createAsyncCommandRPC(2400,
                                                         "eight");
    SeMa4gCommand command09 = this.createAsyncCommandRPC(5100,
                                                         "nine");
    SeMa4gCommand command10 = this.createAsyncCommandRPC(200,
                                                         "ten");

    try {
      SeMa4g.builder()
            .addInitCommand(super.createInitCommand())
            .addFinalCommand(super.createFinalCommand())
            .add(command01)
            .add(command02.dependingOn(command06))
            .add(command03.dependingOn(command08,
                                       command05))
            .add(command04.dependingOn(command02))
            .add(command05)
            .add(command06.dependingOn(command04,
                                       command07,
                                       command09))
            .add(command07)
            .add(command08)
            .add(command09)
            .add(command10)

            .build()
            .run();
    } catch (SeMa4gException e) {
      fp.add(new Label("SeMa4g has detected a cycle dependency. A exception is thrown and the context is not started!"));
    }
  }
}
