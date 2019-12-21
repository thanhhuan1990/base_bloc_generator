package base_bloc_generator.action

import base_bloc_generator.builder.TemplateBuilder
import base_bloc_generator.model.Bloc
import base_bloc_generator.util.FlutterUtils
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidatorEx
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory
import com.intellij.openapi.actionSystem.*

class GenerateBlocAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: throw IllegalStateException("Cannot find project")
        val projectName = FlutterUtils.readProjectName(project)
            ?: throw IllegalStateException("Cannot find flutter project name")

        val name = Messages.showInputDialog(
            "BLoC Name",
            "New Flutter BLoC",
            null,
            null,
            SimpleClassNameInputValidator(project)
        )

        if (name?.isBlank() != false) {
            return
        }

        val bloc = Bloc.build(name, projectName)

        val directory = event.getData(LangDataKeys.PSI_ELEMENT) as PsiDirectory
        if (directory.findSubdirectory(bloc.name) != null) {
            Messages.showErrorDialog("File already exists", "Flutter Base Bloc")
            return
        }

        WriteCommandAction.runWriteCommandAction(event.project) {
            val blocDirectory = directory.createSubdirectory(bloc.name)
            TemplateBuilder.build(bloc, project, blocDirectory)
        }
    }

    class SimpleClassNameInputValidator(private val project: Project) : InputValidatorEx {
        override fun checkInput(input: String): Boolean {
            return getErrorText(input) == null
        }

        override fun canClose(p0: String?): Boolean {
            return true
        }

        override fun getErrorText(input: String): String? {
            if (input.contains(".") || input.contains(" ") || input.toLowerCase() != input) {
                return "Must only contain lowercase characters and underscores"
            }

            return null
        }
    }
}