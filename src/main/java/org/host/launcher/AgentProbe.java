package org.host.launcher;

import com.intellij.execution.Executor;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;
import com.intellij.openapi.components.ServiceManager;
import org.host.launcher.state.MultiTextConfigState;

public class AgentProbe extends JavaProgramPatcher {

    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {

        RunConfiguration runConfiguration = (RunConfiguration) configuration;

        if (runConfiguration instanceof ApplicationConfiguration) {
            MultiTextConfigState configState = ServiceManager.getService(MultiTextConfigState.class);
            ParametersList vmParametersList = javaParameters.getVMParametersList();
            vmParametersList.addParametersString(configState.getSelectedJavaContent());
        }
    }


}
