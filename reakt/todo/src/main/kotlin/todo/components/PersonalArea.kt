package todo.components

import addressServer
import com.github.andrewoma.react.*
import createLogin
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document

/**
 * Created by 4 on 11.06.2016.
 */
data class UserProps(
        val email: String,
        val pass: String,
        val date: String,
        var ip: String,
        var countInput: String,
        var status: String,
        var id: String
)

class PersonalArea : ComponentSpec<UserProps, String>() {

    companion object {
        val factory = react.createFactory(PersonalArea())
    }

    override fun initialState(): String? {
        return ""
    }

    override fun Component.render() {
        div {
            h3 {
                span ({className = "label label-success"}){
                    text("Добро пожаловать, ${props.email}");
                }
            }
            button ({
                className = "btn btn-danger"
                onClick = {
                    exit(props);
                    react.render(createLogin(), document.getElementById("app")!!)
                }
            }){ text("Выйти") }

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

fun createPersonalArea(userProps: UserProps)
        = PersonalArea.factory(Ref(userProps))