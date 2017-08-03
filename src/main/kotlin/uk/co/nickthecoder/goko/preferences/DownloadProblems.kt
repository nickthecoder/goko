package uk.co.nickthecoder.goko.preferences

import javafx.application.Platform
import javafx.scene.control.Alert
import uk.co.nickthecoder.goko.util.Unzipper
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.ResourceParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.child
import java.io.File
import java.io.FileOutputStream

class DownloadProblems(val problemsDirectory: File) : AbstractTask() {

    override val taskD = TaskDescription("downloadProblems")

    val choicesP = ChoiceParameter<DownloadInfo>("goProblems", value = DownloadInfo("", "", true))

    val nameP = StringParameter("name")

    val urlP = ResourceParameter("location")

    val isZipFileP = BooleanParameter("isZipFile")

    init {
        taskD.addParameters(choicesP, nameP, urlP, isZipFileP)

        choicesP.listen {
            nameP.value = choicesP.value?.name ?: ""
            urlP.stringValue = choicesP.value?.urlString ?: ""
            isZipFileP.value = choicesP.value?.isZip ?: true
        }

        addChoice("", "")
        addChoice("Testing", "http://nickthecoder.co.uk/public/test.zip")
        addChoice("Fail", "http://nickthecoder.co.uk/public/test2.zip")
        addChoice("Go Game Guru", "https://gogameguru.com/i/go-problems/download/weekly-go-problems.zip")
        addChoice("Qi Jing Zhong Miao", "http://dl.u-go.net/problems/qjzm-a.zip")
        addChoice("Xuan Xuan Qi Jing", "http://dl.u-go.net/problems/xxqj.zip")
        addChoice("Guan Zi Pu - Set 1", "http://dl.u-go.net/problems/gzp1.zip")
        addChoice("Guan Zi Pu - Set 2", "http://dl.u-go.net/problems/gzp2.zip")
        addChoice("Guan Zi Pu - Set 3", "http://dl.u-go.net/problems/gzp3.zip")
    }

    override fun customCheck() {
        super.customCheck()
        val directory = File(problemsDirectory, nameP.value)

        if (directory.exists()) {
            throw ParameterException(nameP, "Already downloaded")
        }
    }

    private fun addChoice(name: String, urlString: String, isZip: Boolean = true) {
        choicesP.addChoice(name, DownloadInfo(name, urlString, isZip), name)
    }

    override fun run() {
        val directory = File(problemsDirectory, nameP.value)
        val url = urlP.value!!.url

        try {
            val stream = url.openStream()
            with(stream) {

                if (isZipFileP.value == true) {
                    val unzipper = Unzipper()
                    unzipper.unzip(stream, directory)
                    stream.close()

                } else {
                    val buffer = ByteArray(1024)

                    val file = problemsDirectory.child(nameP.value, nameP.value + ".sgf")
                    file.parentFile.mkdirs()

                    val fos = FileOutputStream(file)
                    with(fos) {
                        var len = stream.read(buffer)
                        while (len > 0) {
                            fos.write(buffer, 0, len)
                            len = stream.read(buffer)
                        }
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()

            Platform.runLater {
                val alert = Alert(Alert.AlertType.ERROR)
                alert.title = "Error"
                alert.headerText = null
                alert.contentText = "Download failed. The file may not exist."
                alert.showAndWait()
            }
        }
    }

    data class DownloadInfo(val name: String, val urlString: String, val isZip: Boolean = true)
}
