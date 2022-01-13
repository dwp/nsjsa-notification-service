package uk.gov.dwp.jsa.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import uk.gov.dwp.jsa.security.roles.Role;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.EMPTY_LIST;
import static uk.gov.dwp.jsa.security.roles.Role.CITIZEN;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockUser> {

    @Override
    public SecurityContext createSecurityContext(final WithMockUser withUser) {
        String firstName = withUser.value();
        if (firstName == null) {
            throw new IllegalArgumentException(withUser + " cannot have null firstName");
        }

        Role role = withUser.role();
        final List<SimpleGrantedAuthority> authorities;
        if (CITIZEN.equals(role)) {
            authorities = EMPTY_LIST;
        } else {
            authorities = Collections.singletonList(new SimpleGrantedAuthority(role.name()));
        }

        Authentication authentication = new AuthenticationToken(withUser.value(), withUser.staffNumber(),
                withUser.token(), authorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
