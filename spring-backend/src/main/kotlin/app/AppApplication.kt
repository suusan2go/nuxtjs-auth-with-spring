package app

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore

@SpringBootApplication
@EnableAuthorizationServer
class AppApplication: WebSecurityConfigurerAdapter(){
    @Bean
    fun tokenStore(): TokenStore = InMemoryTokenStore()

    @Bean
    fun tokenService(): ResourceServerTokenServices = DefaultTokenServices().also { it.setTokenStore(tokenStore()) }
}

fun main(args: Array<String>) {
    SpringApplication.run(AppApplication::class.java, *args)
}
