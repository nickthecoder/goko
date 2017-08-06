package uk.co.nickthecoder.goko.util


import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class Unzipper {

    fun unzip(zipFile: File, outputDirectory: File) {
        unzip(zipFile.inputStream(), outputDirectory)
    }

    fun unzip(inputStream: InputStream, outputDirectory: File) {

        val buffer = ByteArray(1024)

        //get the zip file content
        val zis = ZipInputStream(inputStream)
        //get the zipped file list entry
        var ze: ZipEntry? = zis.nextEntry

        while (ze != null) {

            val newFile = File(outputDirectory.path + File.separator + ze.name)
            println("File $newFile")

            File(newFile.parent).mkdirs()

            if (!ze.isDirectory) {
                val fos = FileOutputStream(newFile)
                var len = zis.read(buffer)
                while (len > 0) {
                    fos.write(buffer, 0, len)
                    len = zis.read(buffer)
                }
                fos.close()
            }

            ze = zis.nextEntry
        }

        println( "Closing")
        zis.closeEntry()
        zis.close()
        println( "Unzip complete")
    }

}
