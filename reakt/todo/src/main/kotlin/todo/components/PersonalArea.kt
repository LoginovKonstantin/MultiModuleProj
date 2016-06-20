package todo.components

import com.github.andrewoma.react.*
import createLogin
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
        var status: String
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
                    deleteCookie();
                    react.render(createLogin(), document.getElementById("app")!!)
                }
            }){ text("Выйти") }

        }
    }

    fun deleteCookie() {
        js("var date = new Date(); date.setTime(date.getTime() - 1);")
        document.cookie = "${props.email} = ; expires = +' ${js("date.toGMTString()")}';"
        document.cookie = "${props.pass} = ; expires = +' ${js("date.toGMTString()")}';"
        console.log(document.cookie)
    }
}

fun createPersonalArea(userProps: UserProps)
        = PersonalArea.factory(Ref(userProps))