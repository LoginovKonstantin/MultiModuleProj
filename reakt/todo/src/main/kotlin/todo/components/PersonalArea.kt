package todo.components

import addressServer
import com.github.andrewoma.react.*
import createLogin
import org.w3c.xhr.XMLHttpRequest
import java.util.*
import kotlin.browser.document
import kotlin.browser.window

/**
 * Created by 4 on 11.06.2016.
 */
data class UserProps(
        val email: String,
        val pass: String,
        val date: String,
        var ip: String,
        var countInput: String,
        var id: String
)

data class PersonalAreaState(var listNameChat: ArrayList<String>)

class PersonalArea : ComponentSpec<UserProps, PersonalAreaState>() {

    companion object {
        val factory = react.createFactory(PersonalArea())
    }

    override fun initialState(): PersonalAreaState? {
        return PersonalAreaState(listNameChat = getNamesChats())
    }

    override fun Component.render() {
        div ({className = "left-n-right-side"}){
            div({}){
                h2({className = "welcome"}) {
                    span ({className = "label label-success"}){
                        text("Добро пожаловать, ${props.email}");
                    }
                }
                button ({
                    className = "btn-exit btn btn-danger"
                    onClick = {
                        window.clearInterval(numberInterval)
                        exit(props);
                        react.render(createLogin(), document.getElementById("app")!!)
                    }
                }){ text("Выйти") }
            }
            ListChats(state.listNameChat, props)
        }
    }

    private fun exit(props: UserProps) {
        document.cookie = "id = ; expires = ${js("new Date(0).toUTCString()")}"
        val req = XMLHttpRequest()
        req.open("GET", "$addressServer/exit/${props.email}")
        req.onload = { }
        req.send()
    }

}

fun getNamesChats(): ArrayList<String>{
    val chatNames: ArrayList<String> = arrayListOf()
    val req = XMLHttpRequest()
    req.open("GET", "$addressServer/getExistChats", false)
    req.onload = {
        if(req.responseText.equals("\"getExistChatsFail\"")){
            println("Чатов нет")
        }else{
            (req.responseText.replace("\"", "").split(";")).forEach { chatNames.add(it) }
        }
    }
    req.send()
    return chatNames
}

fun createPersonalArea(userProps: UserProps)
        = PersonalArea.factory(Ref(userProps))