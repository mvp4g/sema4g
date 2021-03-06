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

import com.github.mvp4g.sema4g.client.command.AsyncCommand;
import com.github.mvp4g.sema4g.client.command.FinalCommand;
import com.github.mvp4g.sema4g.client.command.InitCommand;
import com.github.mvp4g.sema4g.client.command.SeMa4gCommand;
import com.github.mvp4g.sema4g.client.command.SyncCommand;
import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import de.gishmo.gwt.sema4g.example.client.service.RpcFailingServiceAsync;
import de.gishmo.gwt.sema4g.example.client.service.RpcServiceAsync;
import com.github.mvp4g.sema4g.rpc.client.command.proxy.AsyncCallbackProxy;

public abstract class AbstractCase {
//
//  private static final String JSON_URL    = GWT.getModuleBaseURL() + "jsonMessage?";
//  private static final String REQUEST_URL = GWT.getModuleBaseURL() + "message?";

  final static String FINALCOMMAND_DESCRIPTION        = "<br><br>The FinalCommand will close the popup and show a JavaScript alert depending on the server responses. If at least one command ends in error, the context will also ends in error.";
  final static String INITCOMMAND_DESCRIPTION         = "<br><br>The InitCommand will open a popup.";
  final static String SERVICE_DESCRIPTION             = "<br><br>The createAsyncCommand-method has two parameters. The first parameter is the name of the command and the second parameter is the duration, the server will wait.";
  final static String SERVICE_FAILURE_DESCRIPTION     = "<br><br>The createAsyncFailureCommand-method has two parameters. The first parameter is the name of the command and the second parameter is the duration, the server will wait. This service will throw an exception on the sercer side";
  final static String SERVICE_SYNCHRONOUS_DESCRIPTION = "<br><br>The createSyncCommand-method has two parameters. The first parameter is the name of the command and the second parameter is the duration, the client will wait. This service does not call the server!";

  /**
   * Create a remote service proxy to talk to the server-side Greeting service.
   */
  private final RpcServiceAsync        service01 = RpcServiceAsync.Util.getInstance();
  private final RpcFailingServiceAsync service02 = RpcFailingServiceAsync.Util.getInstance();
  String     labelText;
  String     descriptionText;
  String     startText;
  String     successText;
  String     errorText;
  FlowPanel  fp;
  PopupPanel popup;
  //  private MyBeanFactory myBeanFactory = GWT.create(MyBeanFactory.class);
  private Duration duration;

  AbstractCase(FlowPanel fp,
               PopupPanel popup) {
    this.fp = fp;
    this.popup = popup;
  }

  public abstract void createContextAndRun();

  public String getDescriptionText() {
    return descriptionText;
  }

  public String getLabelText() {
    return labelText;
  }

  /**
   * Creates an instance of an {@link InitCommand} to use in a Example.
   * <br><br>
   * The InitCommand opens a popup to lock the screen.
   *
   * @return instance of InitCommand
   */
  InitCommand createInitCommand() {
    return () -> {
      duration = new Duration();
      popup.center();
    };
  }

  /**
   * Creates an instance of a {@link FinalCommand} to use in a Example.
   * <br><br>
   * The FinalCommand close the popup to unlock the screen and sets
   * some information in the related fields.
   *
   * @return instance of FinalCommand
   */
  FinalCommand createFinalCommand() {
    return new FinalCommand() {
      @Override
      public void onSuccess() {
        String executionTimeMessage = executionTimeValue();
        popup.hide();
        fp.add(createLabel(successText));
        fp.add(executionTime(executionTimeMessage));
        Window.alert("Execution ended successfully ...\n\n" + executionTimeMessage);
      }

      @Override
      public void onFailure() {
        String executionTimeMessage = executionTimeValue();
        popup.hide();
        fp.add(createLabel(errorText));
        fp.add(executionTime(executionTimeMessage));
      }
    };
  }

  private Label createLabel(String value) {
    Label label = new Label(value);
    label.getElement()
         .getStyle()
         .setMargin(4,
                    Style.Unit.PX);
    return label;
  }


//  /**
//   * Creates an instance of an {@link SeMa4gAsyncCommand} command.
//   * <br><br>
//   * The command will call a service using a RequestBuilder on the service which waits
//   * the number of milliseconds before returning to the client
//   *
//   * @param waitTime number of milliseconds the server waits before returning ot the client
//   * @param name identifier of the service
//   * @return instance of FinalCommand
//   */
//  Command createAsyncCommandRequestBuilder(final long waitTime,
//                                                 final String name) {
//    return new SeMa4gAsyncCommand() {
//      @Override
//      public void execute() {
//        // URL
//        String url = JSON_URL + "wait=" + Long.toString(waitTime);
//        url = URL.encode(url);
//
//        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
//                                                    url);
//        try {
//          @SuppressWarnings("unused")
//          Request request = builder.sendRequest(null,
//                                                new RequestCallbackProxy(this,
//                                                                         new RequestCallback() {
//                                                                           @Override
//                                                                           public void onResponseReceived(Request request,
//                                                                                                          Response response) {
//                                                                             if (200 == response.getStatusCode()) {
//                                                                               Message message = deserializeMessageFromJSON(response.getText());
//                                                                               fp.add(createLabel(message.getMessage()));
//                                                                               GWT.log("success service fourteen");
//                                                                             } else {
//                                                                               fp.add(createLabel("Ups: Service "+ name + " failure"));
//                                                                             }
//                                                                           }
//                                                                           @Override
//                                                                           public void onError(Request request,
//                                                                                               Throwable exception) {
//                                                                             fp.add(createLabel("Ups: Service " + name + " failure"));
//                                                                           }
//                                                                         }));
//        } catch (RequestException e) {
//          fp.add(createLabel("Ups: Service Fourteen failure"));
//        }
//      }
//    };
//  }

  private Label executionTime(String executionTimeMessage) {
    return createLabel(executionTimeMessage);
  }

  private String executionTimeValue() {
    return "Exection Time: " + Integer.toString(duration.elapsedMillis()) + " ms.";
  }

  /**
   * Creates an instance of an {@link AsyncCommand} command.
   * <br><br>
   * The command will call a RPC service on the service which waits
   * the number of milliseconds before returning to the client
   *
   * @param waitTime number of milliseconds the server waits before returning ot the client
   * @param name     identifier of the service
   * @return instance of FinalCommand
   */
  SeMa4gCommand createAsyncCommandRPC(final long waitTime,
                                      final String name) {
    return new AsyncCommand() {
      @Override
      public void execute() {
        fp.add(createLabel("Service (RPC) " + name + " started"));
        service01.callServer(waitTime,
                             name,
                             new AsyncCallbackProxy<String>(this) {
                               @Override
                               protected void onProxyFailure(Throwable caught) {
                                 fp.add(createLabel("Ups: service " + name + " failure"));
                               }

                               @Override
                               protected void onProxySuccess(String result) {
                                 fp.add(createLabel(result));
                                 GWT.log("service " + name + " successful executed");
                               }
                             });
      }
    };
  }

  /**
   * Creates an instance of an {@link AsyncCommand} command.
   * <br><br>
   * The command will call a RPC service on the service which waits
   * the number of milliseconds before returning to the client
   *
   * @param waitTime number of milliseconds the server waits before returning ot the client
   * @param name     identifier of the service
   * @return instance of FinalCommand
   */
  SeMa4gCommand createAsyncFailingCommandRPC(final long waitTime,
                                             final String name) {
    return new AsyncCommand() {
      @Override
      public void execute() {
        fp.add(createLabel("Service (RPC - Failure) " + name + " started"));
        service02.callServer(waitTime,
                             name,
                             new AsyncCallbackProxy<String>(this) {
                               @Override
                               protected void onProxyFailure(Throwable caught) {
                                 fp.add(createLabel("Yeah: service " + name + " failure"));
                               }

                               @Override
                               protected void onProxySuccess(String result) {
                                 fp.add(createLabel(result));
                                 GWT.log("Ups: service " + name + " successful executed .. That's what we not expected.");
                               }
                             });
      }
    };
  }

  /**
   * Creates an instance of an {@link SyncCommand} command with a Timer, to simulate some actions.
   *
   * @param name identifier of the service
   * @return instance of FinalCommand
   */
  SeMa4gCommand createSyncCommand(final String name) {
    return new SyncCommand() {
      @Override
      public void execute() {
        fp.add(createLabel("Service " + name + " (synchron) returned"));
      }
    };
  }

//------------------------------------------------------------------------------

//  private Message deserializeMessageFromJSON(String json) {
//    AutoBean<Message> bean = AutoBeanCodex.decode(myBeanFactory,
//                                                  Message.class,
//                                                  json);
//    return bean.as();
//  }
//
////------------------------------------------------------------------------------
//
//  interface MyBeanFactory
//    extends AutoBeanFactory {
//
//    AutoBean<Message> message();
//
//  }

}
