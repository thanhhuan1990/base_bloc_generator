package base_bloc_generator.builder

import base_bloc_generator.model.Bloc
import com.google.common.base.CaseFormat
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory

object TemplateBuilder {

    enum class Template {
        Events, State, Widget, Bloc
    }

    private object Properties {
        const val ClassName = "CLASS_NAME"
        const val ClassNameLower = "CLASS_NAME_LOWER"
    }

    fun build(bloc: Bloc, project: Project, destinationDirectory: PsiDirectory) {
        val manager = FileTemplateManager.getInstance(project)
        val properties = buildProperties(manager.defaultProperties, bloc)

        mapTemplates(bloc).forEach { template ->
            val fileTemplate = manager.getInternalTemplate(template.key.name.toLowerCase())
            FileTemplateUtil.createFromTemplate(fileTemplate, template.value, properties, destinationDirectory)
        }
    }

    private fun mapTemplates(bloc: Bloc) = Template.values().associate {
        when (it) {
            Template.State -> Pair(it, bloc.stateFilename)
            Template.Events -> Pair(it, bloc.eventsFilename)
            Template.Bloc -> Pair(it, bloc.blocFilename)
            Template.Widget -> Pair(it, bloc.screenFilename)
        }
    }

    private fun buildProperties(properties: java.util.Properties, bloc: Bloc) = properties.apply {
        put(Properties.ClassName, bloc.className)
        put(Properties.ClassNameLower, CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, bloc.name))
    }

}