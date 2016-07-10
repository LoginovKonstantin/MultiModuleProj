import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat

/**
 * Created by 4 on 06.07.2016.
 */
class Message: Comparable<Message>{

    @SerializedName("d")
    var date: String

    @SerializedName("a")
    var author: String

    @SerializedName("t")
    var text: String

    constructor(date: String, author: String, text: String){
        this.date = date
        this.author = author
        this.text = text
    }

    override fun compareTo(other: Message): Int {
        val dateFormat = SimpleDateFormat("HH:mm:ss dd.MM.yyyy")
        if(dateFormat.parse(this.date) > dateFormat.parse(other.date))
            return 1
        else if (dateFormat.parse(this.date) < dateFormat.parse(other.date))
            return -1;
        else return 0;
    }
}