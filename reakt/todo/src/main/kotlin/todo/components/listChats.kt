package todo.components

import addressServer
import com.github.andrewoma.react.*
import getCookie
import org.w3c.xhr.XMLHttpRequest
import java.util.*
import kotlin.browser.document

/**
 * Created by 4 on 25.06.2016.
 */

data class ListChatsState(var nameChat: String, var message: String, var items: ArrayList<String>)
data class ListChatsProps(var items: ArrayList<String>, var user: UserProps)

class ListChats : ComponentSpec<ListChatsProps, ListChatsState>() {

    companion object {
        val factory = react.createFactory(ListChats())
    }

    override fun initialState(): ListChatsState? {
        return ListChatsState( nameChat = "", message = "", items = props.items)
    }

    override fun Component.render() {
        div({className = "container-fluid left-n-right-side"}){
            div({className = "row-fluid left-n-right-side"}){
                div({className = "left-side"}){
                    div({className = "input-group create-chat-input"}){
                        input ({
                            className = "form-control"
                            onChange = {functionSearch(it.currentTarget)}
                            defaultValue = ""
                            placeholder = ""
                        }) {}
                        span ({className = "input-group-btn"}){
                            button({
                                className = "btn btn-primary"
                                onClick = {checkNameField()}
                            }) {
                                span ({ className = "glyphicon glyphicon-plus" }) {};
                            }
                        }
                    }
                    ul({
                        className = "list-group";
                    }) {
                        state.items.forEach {
                            li({className = "list-group-item"}) {
                                text(it)
                                val nameConcreteChat = it;
                                button({
                                    className = "btnEntranceChat"
                                    onClick = {
                                        println(nameConcreteChat)
                                        document.cookie = "id = ${getCookie("id")}; expires = ${js("new Date(new Date().getTime() + 60 * 1000 * 5).toUTCString()")}"
                                        js("if (document.getElementById('concreteChat')) {document.getElementById('concreteChat').parentNode.removeChild(document.getElementById('concreteChat'));}")
                                        react.render(createChat(UserAndChatProps(props.user, nameConcreteChat)), document.getElementById("containerChat")!!)
                                        document.getElementById("div-list-messages")!!.scrollTop = document.getElementById("div-list-messages")!!.scrollHeight
                                    }
                                }) {
                                    span ({className = "glyphicon glyphicon-log-in"}) {  }
                                }
                            }
                        }
                    }
                    span({
                        var access = "default"
                        if(state.message.equals("Имя чата < 2 символов") ||
                                (state.message.equals("Такой чат уже создан")) ||
                                (state.message.equals("Имя чата > 20 символов"))){
                            access = "danger"
                        }
                        if(state.message.equals("Чат создан")){
                            access = "success"
                        }
                        className = "label label-$access"
                    }){
                        text (state.message)
                    }
                }
                div({className = "span9 right-side"; id = "containerChat"}){

                }
            }
        }
    }

    private fun functionSearch(event: EventTarget) {
        val searchQuery = event.value.toLowerCase()
        val displayedChats = props.items.filter {
            val searchValue = it.toLowerCase()
            searchValue.indexOf(searchQuery) != -1
        }
        state = ListChatsState(event.value, state.message, ArrayList(displayedChats))
    }

    private fun checkNameField() {
        document.cookie = "id = ${getCookie("id")}; expires = ${js("new Date(new Date().getTime() + 60 * 1000 * 5).toUTCString()")}"
        if(state.nameChat.length < 2){
            state = ListChatsState("", "Имя чата < 2 символов", state.items)
        }else if (state.nameChat.length > 20){
            state = ListChatsState("", "Имя чата > 20 символов", state.items)
        }else{
            requestNewChat()
            state = ListChatsState("", "", state.items)
        }
    }

    private fun requestNewChat(){
        val req = XMLHttpRequest()
        req.open("GET", "$addressServer/newChat/${state.nameChat}/${props.user.email}")
        req.onload = {
            if(req.responseText.equals("\"createNewChatFail\"")){
                state = ListChatsState("", "Такой чат уже создан", state.items)
            }else{
                props.items = getNamesChats()
                state = ListChatsState("", "Чат создан", props.items)
            }
        }
        req.send()
    }

}

fun Component.ListChats(listChat: ArrayList<String>, user: UserProps): Component {
    return constructAndInsert(Component({ ListChats.factory(Ref(ListChatsProps(listChat, user))) }))
}