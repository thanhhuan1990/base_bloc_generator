package base_bloc_generator.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.representer.Representer
import org.yaml.snakeyaml.resolver.Resolver
import java.io.File
import java.io.IOException

object FlutterUtils {
    fun readProjectName(project: Project): String? {
        val pubspec = VfsUtil.findFileByIoFile(File("${project.basePath}/pubspec.yaml"), false) ?: return null
        val properties = readPubspecFileToMap(pubspec)
        return properties?.get("name") as String
    }

    @Throws(IOException::class)
    private fun readPubspecFileToMap(pubspec: VirtualFile): Map<String, Any>? {
        val contents = String(pubspec.contentsToByteArray(true /* cache contents */))
        return loadPubspecInfo(contents)
    }

    private fun loadPubspecInfo(yamlContents: String): Map<String, Any>? {
        val yaml = Yaml(SafeConstructor(), Representer(), DumperOptions(), object : Resolver() {
            override fun addImplicitResolvers() {
                this.addImplicitResolver(Tag.BOOL, BOOL, "yYnNtTfFoO")
                this.addImplicitResolver(Tag.NULL, NULL, "~nN\u0000")
                this.addImplicitResolver(Tag.NULL, EMPTY, null)
                this.addImplicitResolver(Tag("tag:yaml.org,2002:value"), VALUE, "=")
                this.addImplicitResolver(Tag.MERGE, MERGE, "<")
            }
        })

        try {
            return yaml.load<Any>(yamlContents) as Map<String, Any>!!
        } catch (e: Exception) {
            return null
        }

    }
}