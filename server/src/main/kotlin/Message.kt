import com.google.gson.annotations.SerializedName

/**
 * Created by 4 on 06.07.2016.
 */
class Message{

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
}