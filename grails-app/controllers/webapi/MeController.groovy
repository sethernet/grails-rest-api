package webapi

import grails.converters.JSON
import grails.transaction.Transactional

import org.springframework.security.core.context.SecurityContextHolder
import static org.springframework.http.HttpStatus.*

class MeController {

    static allowedMethods = [digests: "GET"]

    @Transactional
    def digests() {

      def sizeValue = 3;
      def fromValue = 0;

      if (params.size){
        if (params.size.isInteger()){
          sizeValue = params.int('size')
          sizeValue = Math.min(sizeValue, 100)
        }
      }

      if (params.from){
        if (params.from.isInteger()){
          fromValue = params.int('from')
        }
      }

      def authenticatedPerson = Person.findByUsername(SecurityContextHolder.context?.authentication?.principal.username)
      def digestList = authenticatedPerson.digests

      def c = Digest.createCriteria()
      def results = c.list (max: sizeValue, offset: fromValue) {
            eq("customer", authenticatedPerson)
      }

      render results as JSON

    }

}
