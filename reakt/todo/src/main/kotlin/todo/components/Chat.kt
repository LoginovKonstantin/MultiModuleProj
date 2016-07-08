package todo.components

import addressServer
import com.github.andrewoma.react.*
import getCookie
import jquery.jq
import org.w3c.xhr.XMLHttpRequest
import java.util.*
import kotlin.browser.document

/**
 * Created by 4 on 05.07.2016.
 */
data class UserAndChatProps(var user: UserProps, var nameChat: String)
data class ChatState(var messages: ArrayList<String>)

class Chat : ComponentSpec<UserAndChatProps, ChatState>() {

    var message = ""

    companion object {
        var factory = react.createFactory(Chat())
    }

    override fun initialState(): ChatState? {
        return ChatState(messages = getMessagesFromServer())
    }

    override fun Component.render() {
        div({className = "chat"; id = "concreteChat"}){
            div{
                h3 {span ({className = "label label-info"}) { text("Комната: ${props.nameChat}") }}
            }
            div ({
                id = "div-list-messages"
                className = "controllers well";
            }){
                state.messages.forEach {
                    div { text(it) }
                }
//                for(i in 0..55){
//                    div{text ("Message $i")}
//                }
            }
            div({className = "input-group"}){
                input ({
                    id = "inputNewMess"
                    className = "form-control"
                    onChange = {message = it.currentTarget.value}
                    defaultValue = ""
                }) {}
                span ({className = "input-group-btn"}){
                    button({
                        className = "btn btn-success"
                        onClick = { createNewMessage(); }
                    }) {
                        span ({ className = "glyphicon glyphicon-envelope" }) {};
                    }
                }
            }
        }
    }

    fun getMessagesFromServer(): ArrayList<String>{
        val req = XMLHttpRequest()
        req.open("GET", "$addressServer/getMessages/${props.nameChat}", false)

        req.onload = {


            parse(req)


        }
        req.send()
        return arrayListOf()
    }
    fun parse(req: XMLHttpRequest){

        println(req.responseText)

        var x = JSON.parse<Unit>(req.responseText).asDynamic()
        x.iterator().forEach {
            println(it)
            var a: Mess = it[1]
            println(a.d)
        }

    }

    fun createNewMessage() {
        document.cookie = "id = ${getCookie("id")}; expires = ${js("new Date(new Date().getTime() + 60 * 1000 * 5).toUTCString()")}"
        if(!message.equals("")){
            state.messages.add(message)
            val req = XMLHttpRequest()
            req.open("GET", "$addressServer/newMessage/$message/${props.nameChat}/${props.user.email}")
            req.onload = {}
            req.send()
            message = "";
        }
    }
}

data class Mess(var a: String, var d: String, var t: String)

class Messages(var i: Int, var array: ArrayList<Mess>)

fun createChat(userAndChat: UserAndChatProps)
        = Chat.factory(Ref(userAndChat))
