package todo.components

import addressServer
import com.github.andrewoma.react.*
import getCookie
import org.w3c.xhr.XMLHttpRequest
import java.util.*
import kotlin.browser.document
import kotlin.browser.window

/**
 * Created by 4 on 05.07.2016.
 */
data class UserAndChatProps(var user: UserProps, var nameChat: String)
data class ChatState(var messages: ArrayList<Mess>, var userOnEmail: UserOnEmail)
data class Mess(var a: String, var d: String, var t: String)
data class UserOnEmail(var ip: String, var countInput: String, var date: String, var userName: String)

var numberInterval: Int = 0;

class Chat : ComponentSpec<UserAndChatProps, ChatState>() {

    var message = ""
    val x = window.setInterval({updateChat()},2000)

    fun updateChat(){
        numberInterval = x
        val updateListMessages = getMessagesFromServer()
        if(updateListMessages.size > state.messages.size){
            state = ChatState(updateListMessages, state.userOnEmail)
            document.getElementById("div-list-messages")!!.scrollTop = document.getElementById("div-list-messages")!!.scrollHeight
        }
    }

    companion object {
        var factory = react.createFactory(Chat())
    }

    override fun initialState(): ChatState? {
        window.clearInterval(x-1)
        return ChatState(messages = getMessagesFromServer(), userOnEmail = UserOnEmail("", "", "", ""))
    }

    override fun Component.render() {
        div({ className = "chat"; id = "concreteChat" }) {
            div {
                div({className = "row"}) {
                    div({className = "left-header-room col-md-3"}){
                        h3{span ({ className = "label label-info" }) { text("Комната: ${props.nameChat}") }}
                    }
                    div({className = "col-md-9"}){
                        if(!state.userOnEmail.date.equals("")){
                            div ({className = "input-group"}) {
                                div({className = "my-alert alert alert-warning"}){
                                    text("Инфо о ${state.userOnEmail.userName} - IP: ${state.userOnEmail.ip}, " +
                                            "дата регистрации: ${state.userOnEmail.date}, заходил(а) раз: ${state.userOnEmail.countInput} ")
                                    button({
                                        className = "btn-inalert btn btn-deffault"
                                        onClick = {
                                            document.cookie = "id = ${getCookie("id")}; expires = ${js("new Date(new Date().getTime() + 60 * 1000 * 5).toUTCString()")}"
                                            state = ChatState(state.messages, UserOnEmail("", "", "", ""))
                                        }
                                    }) {
                                        span ({ className = "glyphicon glyphicon-remove" }) {};
                                    }
                                }
                            }
                        }
                    }
                }
            }
            div ({
                id = "div-list-messages"
                className = "controllers well";
            }) {
                state.messages.forEach {
                    div({ className = "clearfix" }) {
                        div({className = "header"}){
                            strong({className = "primary-font"}) {
                                val author = it.a
                                a({href = "#";  ;onClick = { getInformationUser(author) }; })
                                { text(it.a) }
                            }
                            small({className = "pull-right text-muted"}){
                                span({className = "glyphicon glyphicon-time"})
                                text(it.d)
                            }
                            p { text(it.t) }
                        }
                    }
                }
            }
            div({ className = "input-group" }) {
                input ({
                    id = "inputNewMess"
                    onKeyDown = {if(it.keyCode == 13) createNewMessage()}
                    autoFocus = true
                    className = "form-control"
                    onChange = { message = it.currentTarget.value }
                    defaultValue = ""
                }) {}
                span ({ className = "input-group-btn" }) {
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

    private fun getInformationUser(userName: String) {
        document.cookie = "id = ${getCookie("id")}; expires = ${js("new Date(new Date().getTime() + 60 * 1000 * 5).toUTCString()")}"
        val req = XMLHttpRequest()
        req.open("GET", "$addressServer/getUser/$userName", false)
        req.onload = {
            if(!req.responseText.equals("")){
                val x = JSON.parse<UserOnEmail>(req.responseText)
                state.userOnEmail.countInput = x.countInput
                state.userOnEmail.ip = x.ip
                state.userOnEmail.date = x.date
                state.userOnEmail.userName = userName
            }
        }
        state = ChatState(state.messages, state.userOnEmail)
        req.send()
    }

    fun getMessagesFromServer(): ArrayList<Mess> {
        val messages: ArrayList<Mess> = arrayListOf()
        val req = XMLHttpRequest()
        req.open("GET", "$addressServer/getMessages/${props.nameChat}", false)
        req.onload = {
            val x = JSON.parse<Unit>(req.responseText)
            x.iterator().forEach {
                val currentMess: Mess = js("it")
                messages.add(Mess(currentMess.a, currentMess.d, currentMess.t))
            }
        }
        req.send()
        return messages
    }

    fun createNewMessage() {
        document.cookie = "id = ${getCookie("id")}; expires = ${js("new Date(new Date().getTime() + 60 * 1000 * 5).toUTCString()")}"
        if (!message.equals("")) {
            val req = XMLHttpRequest()
            req.open("GET", "$addressServer/newMessage/$message/${props.nameChat}/${props.user.email}")
            req.onload = {}
            req.send()
            message = "";
        }
    }
}

fun createChat(userAndChat: UserAndChatProps)
        = Chat.factory(Ref(userAndChat))
