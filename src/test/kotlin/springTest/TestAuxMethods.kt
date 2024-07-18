package springTest

import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths

class TestAuxMethods {
    fun accessAbsolutePath(path: String): String {
        try {
            return Paths.get(path).toAbsolutePath().toString()
        } catch (e: Exception) {
            throw IllegalArgumentException("Resource not found for: $path")
        }
    }

    fun createMultipartFile(filecontent: String): MultipartFile {
        val bytearray = filecontent.toByteArray()
        return MockMultipartFile("file", "file.ps", "text/plain", bytearray)
    }

    fun getFile(path: String): MultipartFile {
        val file = Paths.get(path)
        val filename = file.fileName.toString()
        val contenttype = "text/plain"
        val content = Files.readAllBytes(file)
        return MockMultipartFile(filename, filename, contenttype, content)
    }
}
