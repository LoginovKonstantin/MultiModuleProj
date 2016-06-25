package todo.components

import com.github.andrewoma.react.*

/**
 * Created by 4 on 25.06.2016.
 */

data class ListState(var items: MutableList<String>, var nameChat: String, var message: String)

class ListChats : ComponentSpec<Unit, ListState>() {

    companion object {
        val factory = react.createFactory(ListChats())
    }

    override fun initialState(): ListState? {
        return ListState(items = mutableListOf(), nameChat = "", message = "")
    }

    override fun Component.render() {
        checkChatsByServer()
        div{
            div ({className = "divCreateChat"}){
                input ({
                    className = "inputNameChat form-control"
                    onChange = {state.nameChat = it.currentTarget.value}
                    defaultValue = ""
                    placeholder = ""
                }) {}
            }
            button({
                className = "btn btn-primary"
                onClick = {checkNameField()}
            }) {
                span ({ className = "glyphicon glyphicon-plus" }) {};
            }
        }
    }

    private fun checkNameField() {
        console.log(state.nameChat)
        if(state.nameChat.length > 0){
            state = ListState(mutableListOf(), "", "cool")
        }else{
            state = ListState(mutableListOf(), "", "error")
        }
    }

    private fun checkChatsByServer() {

    }

}

fun Component.listChats(): Component {
    return constructAndInsert(Component({ ListChats.factory(Ref(null)) }))
}