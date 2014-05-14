package grailsspringsecurity
import org.springframework.security.access.annotation.Secured

/*
 If you're using older Grails version like 2.2.x series use the
 following instead:
   import grails.plugin.springsecurity.annotation.Secured

   for newer Grails version
   import org.springframework.security.access.annotation.Secured
 */

class SensitiveContentController {

    @Secured(['ROLE_ADMIN'])
    def index() {
        render "Some sensitive content"
    }
}
