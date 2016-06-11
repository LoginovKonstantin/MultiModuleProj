import com.github.andrewoma.react.*
import org.w3c.xhr.XMLHttpRequest

data class InputState(var email: String, var pass: String, var message: String)

class Login : ComponentSpec<Unit, InputState>() {

    companion object {
        val factory = react.createFactory(Login())
    }

    override fun initialState(): InputState? {
        return InputState("", "", "")
    }

    override fun Component.render() {
        div {
            input ({
                className = "form-control"
                placeholder = "Email"
                onChange = {state.email = it.currentTarget.value}
                defaultValue = state.email
            }){}
            input ({
                type = "password"
                className = "form-control"
                placeholder = "Password"
                onChange = {state.pass = it.currentTarget.value}
                defaultValue = state.pass
            }){}
            div ({className = "divWithBtn"}) {
                button ({
                    className = "btn btn-success"
                    onClick = { if(validInputs()) logIn() }
                })
                { text("Вход") }
            }
            div ({className = "divWithBtn"}) {
                button ({
                    className = "btn btn-success"
                    onClick = { if(validInputs()) registration() }
                })
                { text("Регистрация") }
                br { }
                span ({}){ text(state.message) }
            }
        }
    }

    private fun logIn() {
        val req = XMLHttpRequest()
        req.open("GET", "http://localhost:8080/login/${state.email}/${state.pass}")
        req.onload = {
            if(req.responseText.equals("\"loginFail\"")){
                state = InputState("", "", "Пользователя не существует")
            }else {
                state = InputState("", "", req.responseText)
            }
            console.log(req.responseText)
        }
        req.send()
        console.log("Вход")
    }

    private fun registration(){
        val req = XMLHttpRequest()
        req.open("GET", "http://localhost:8080/registration/${state.email}/${state.pass}")
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