import com.github.andrewoma.react.*
import org.w3c.xhr.XMLHttpRequest
import todo.components.UserProps
import todo.components.createPersonalArea
import kotlin.browser.document

data class InputState(var email: String, var pass: String, var message: String)

val addressServer = "http://localhost:8080"

class Login : ComponentSpec<Unit, InputState>() {

    companion object {
        val factory = react.createFactory(Login())
    }

    override fun initialState(): InputState? {
        return InputState("", "", "")
    }

    override fun Component.render() {
        div {
            if(!getCookie("id").equals("")){
                console.log(getCookie("id"))
                logIn(null, null, getCookie("id"))
            }else{
                input ({
                    className = "loginInput form-control"
                    placeholder = "Email"
                    onChange = {state.email = it.currentTarget.value}
                    defaultValue = state.email
                }){}
                input ({
                    type = "password"
                    className = "loginInput form-control"
                    placeholder = "Password"
                    onChange = {state.pass = it.currentTarget.value}
                    defaultValue = state.pass
                }){}
                div ({className = "divWithBtn"}) {
                    button ({
                        className = "loginBtn btn btn-success"
                        onClick = { if(validInputs()) logIn(state.email, state.pass, null) }
                    }) { text("Вход") }
                }
                div ({className = "divWithBtn"}) {
                    button ({
                        className = "loginBtn btn btn-success"
                        onClick = { if(validInputs()) registration()  }
                    }) { text("Регистрация") }
                    br { }
                    span ({}){ text(state.message) }
                }
            }
        }
    }

    private fun logIn(email: String?, pass: String?, id: String?) {
        var user: String
        var userPropertiesList: List<String> = listOf()
        val req: XMLHttpRequest
        if(email.equals(null) && pass.equals(null)){
            req = XMLHttpRequest()
            req.open("GET", "$addressServer/getUserId/$id")
        }else{
            req = XMLHttpRequest()
            req.open("GET", "$addressServer/login/$email/$pass")
        }
        req.onload = {
            if(req.responseText.equals("\"loginFail\"")){
                state = InputState("", "", "Пользователя не существует")
            }else {
                console.log("по create personalarea")
                user = req.responseText.replace("\"","")
                userPropertiesList = user.split(",")
                document.cookie = "id = ${userPropertiesList[5]}; expires = ${js("new Date(new Date().getTime() + 60 * 1000 * 5).toUTCString()")}"
                react.render(createPersonalArea(UserProps(
                        userPropertiesList[0],//email
                        userPropertiesList[1],//password
                        userPropertiesList[2],//date
                        userPropertiesList[3],//ip
                        userPropertiesList[4],//countInput
                        userPropertiesList[5]//id
                )), document.getElementById("app")!!)
            }
            console.log(req.responseText)
        }
        req.send()
    }

    private fun registration(){
        val req = XMLHttpRequest()
        req.open("GET", "$addressServer/registration/${state.email}/${state.pass}")
        req.onload = {
            if(req.responseText.equals("\"registrationSuccess\"")){
                state = InputState("", "", "Регистрация прошла успешно")
            }else {
                state = InputState("", "", "Пользователь уже существует")
            }
            console.log(req.responseText)
        }
        req.send()
    }

    fun getCookie(cname:String): String {
        var name = cname + "=";
        var ca = document.cookie.split(';');
        for(i in 0..ca.size - 1) {
            var c = ca[i];
            while (c[0]==' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
                return c.substring(name.length,c.length);
            }
        }
        return "";
    }

    private fun validInputs():Boolean {
        if(state.email.equals("") || state.pass.equals("")){
            state = InputState("", "", "Пожалуйста заполните поля")
            return false
        }else{
            return  true
        }
    }
}

fun createLogin() = Login.factory(Ref(null))