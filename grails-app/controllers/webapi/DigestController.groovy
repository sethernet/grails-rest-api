package webapi

import grails.converters.JSON
import grails.transaction.Transactional
import java.security.MessageDigest

import org.springframework.security.core.context.SecurityContextHolder
import static org.springframework.http.HttpStatus.*

class DigestController {

    static allowedMethods = [save: "POST"]

    @Transactional
    def save(DigestCommand entry) {

      if (entry.input == null) {
            response.status = 400

            def error = [
                [
                    status: response.status,
                    field: 'input',
                    'rejected-value': entry.input,
                    message: 'The input field cannot be blank'
                ]
            ]
            def results = [errors: error]
            render results as JSON

            return
        }

        if (entry.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond entry.errors
            return
        }

        def rawMessage = entry.input
        def digest = MessageDigest.getInstance("MD5")
        def md5hash = new BigInteger(1,digest.digest(rawMessage.getBytes())).toString(16).padLeft(32,"0")
        def digestObj = new Digest()

        digestObj.output = md5hash
        digestObj.input = rawMessage

        //def authenticatedPerson = Person.findByUsername(SecurityContextHolder.context?.authentication?.principal.username)

        try {
          def authenticatedPerson = Person.findByUsername(SecurityContextHolder.context?.authentication?.principal.username)
          authenticatedPerson.addToDigests(digestObj)
          authenticatedPerson.save flush:true
          digestObj.save flush:true
          respond digestObj, [status: CREATED]
        } catch (all) {
          response.status = 401
          def error = [
              [
                  status: response.status,
                  field: 'token',
                  message: 'Wrong grant type. Requires a password grant type access token'
              ]
          ]
          def results = [errors: error]
          render results as JSON
          return
        }

    }

}

class DigestCommand {
    String input

    static constraints = {
        input(blank: false)
    }
}
