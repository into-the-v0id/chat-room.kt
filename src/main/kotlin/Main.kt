import org.chatRoom.aggreagte.User
import org.chatRoom.events.user.DeleteUser

fun main(args: Array<String>) {
    val user = User.create(email = "some@mail.com")
    println(user.email)
    println(user.events)

    var newUser = user.changeEmail("other1@mail.com")
    newUser = newUser.changeEmail("other2@mail.com")
    newUser = newUser.changeEmail("other3@mail.com")
    println(newUser.email)
    println(newUser.events)

    val deletedUser = User.applyEvent(
        newUser,
        DeleteUser(modelId = newUser.modelId)
    )
    println(deletedUser)

    val userTwo = User.applyAllEvents(
        null,
        newUser.events
    )
    println(userTwo?.email)
    println(userTwo?.events)

    println(userTwo == newUser)
}
