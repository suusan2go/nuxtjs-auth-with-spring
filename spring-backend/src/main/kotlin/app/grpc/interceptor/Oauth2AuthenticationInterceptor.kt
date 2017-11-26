package app.grpc.interceptor

import io.grpc.*;
import org.lognet.springboot.grpc.GRpcGlobalInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import java.util.Objects;

import com.google.common.base.Strings.nullToEmpty
import io.grpc.ServerCallHandler
import io.grpc.ServerCall


@GRpcGlobalInterceptor
class Oauth2AuthenticationInterceptor(
        @Autowired private val tokenServices: ResourceServerTokenServices
): ServerInterceptor{

    override fun <ReqT, RespT> interceptCall(
            call: ServerCall<ReqT, RespT>,
            headers: Metadata,
            next: ServerCallHandler<ReqT, RespT>): ServerCall.Listener<ReqT> {
        val authHeader = nullToEmpty(headers.get(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)))
        if (!(authHeader.startsWith("Bearer ") || authHeader.startsWith("bearer "))) {
            return next.startCall(call, headers)
        }

        try {
            val token = authHeader.substring(7)

            if (authenticationIsRequired()) {
                val authResult = tokenServices.loadAuthentication(token)


                SecurityContextHolder.getContext().authentication = authResult
            }
        } catch (e: AuthenticationException) {
            SecurityContextHolder.clearContext()


            throw Status.UNAUTHENTICATED.withDescription(e.message).withCause(e).asRuntimeException()
        } catch (e: OAuth2Exception) {
            SecurityContextHolder.clearContext()
            throw Status.UNAUTHENTICATED.withDescription(e.message).withCause(e).asRuntimeException()
        }

        return next.startCall(call, headers)
    }


    private fun authenticationIsRequired(): Boolean {
        val existingAuth = SecurityContextHolder.getContext().authentication
        if (Objects.isNull(existingAuth) || !existingAuth.isAuthenticated) {
            return true
        }

        return existingAuth is AnonymousAuthenticationToken
    }
}