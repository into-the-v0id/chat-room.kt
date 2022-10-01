import org.chatRoom.core.aggreagte.User
import org.chatRoom.core.db.MigrationManager
import org.chatRoom.core.event.user.DeleteUser
import org.chatRoom.core.repository.UserRepository
import org.postgresql.ds.PGPoolingDataSource

fun main(args: Array<String>) {
    val dataSource = PGPoolingDataSource().apply {
        serverNames = arrayOf("localhost")
        databaseName = "app"
        user = "app"
        password = "app"
//        maxConnections = 10
    }

    val migrationManager = MigrationManager(dataSource)
    migrationManager.migrate()


    val userRepository = UserRepository(dataSource.connection)

    val user = User.create(email = "some@mail.com")
    println(user.email)
    println(user.events)

    var newUser = user.changeEmail("other1@mail.com")
    newUser = newUser.changeEmail("other2@mail.com")
    newUser = newUser.changeEmail("other3@mail.com")
    println(newUser.email)
    println(newUser.events)
    userRepository.create(newUser)

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

    val dbUser = userRepository.getById(newUser.modelId)
    println(dbUser?.email)
    println(dbUser?.events)

    userRepository.delete(newUser)
}
