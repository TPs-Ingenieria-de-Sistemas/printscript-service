package springTest

import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths

class TestAuxMethods {
    fun accessAbsolutePath(path: String): String {
         try{
            return Paths.get(path).toAbsolutePath().toString()
        } catch (e: Exception){
            throw IllegalArgumentException("Resource not found for: $path")
        }
    }
/*
    fun createMultipartFile(fileContent: String): MultipartFile {
        val byteArray = fileContent.toByteArray()
        return MockMultipartFile("file", "file.ps", "text/plain", byteArray)
    }

    fun getFile(path: String): MultipartFile {
        val file = Paths.get(path)
        val fileName = file.fileName.toString()
        val contentType = "text/plain"
        val content = Files.readAllBytes(file)
        return MockMultipartFile(fileName, fileName, contentType, content)
    }
   */

}
