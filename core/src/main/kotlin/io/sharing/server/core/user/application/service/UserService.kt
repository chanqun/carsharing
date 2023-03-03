package io.sharing.server.core.user.application.service

import io.sharing.server.core.support.exception.TisError.USER_DUPLICATED_EMAIL
import io.sharing.server.core.support.exception.TisException
import io.sharing.server.core.support.stereotype.UseCase
import io.sharing.server.core.user.application.port.inp.CreateUser
import io.sharing.server.core.user.application.port.inp.CreateUserCommand
import io.sharing.server.core.user.application.port.inp.InactiveUser
import io.sharing.server.core.user.application.port.outp.UserRepository
import io.sharing.server.core.user.domain.User
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

@UseCase
@Transactional
class UserService(
    private val userRepository: UserRepository
) : CreateUser, InactiveUser {
    override fun create(command: CreateUserCommand) {
        userRepository.findByEmail(command.email)?.let { throw TisException(USER_DUPLICATED_EMAIL) }

        with(command) { userRepository.save(User.create(email, firstName, lastName, birthDay)) }
    }

    override fun inactive(id: Long) {
        val user = userRepository.findByIdOrNull(id) ?: throw EntityNotFoundException()

        user.changeToInactive()

        userRepository.save(user)
    }
}
