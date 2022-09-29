import org.chatRoom.aggreagte.User
import org.chatRoom.events.user.DeleteUser

fun main(args: Array<String>) {
    val user = User.create(email = "some@mail.com")

    val newUser = user.changeEmail("other@mail.com")

    println(user.email)
    println(newUser.email)

    println(user.events)
    println(newUser.events)

    val deletedUser = User.applyEvent(
        newUser,
        DeleteUser(modelId = newUser.modelId)
    )
    println(deletedUser)
}
