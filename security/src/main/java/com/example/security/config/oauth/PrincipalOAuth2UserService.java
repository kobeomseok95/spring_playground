package com.example.security.config.oauth;

import com.example.security.config.auth.PrincipalDetails;
import com.example.security.user.User;
import com.example.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    /**
     * 구글로부터 받은 userRequest 데이터에 대한 후처리되는 메서드
     * @param userRequest : 구글 리소스 서버에서 정보들을 얻어온 객체
     *                    구글 로그인 버튼 클릭 > 구글 로그인창 > 로그인 완료 > 코드를 리턴(OAuth2-Client 라이브러리가 받아줌)
     *                    > code를 통해 AccessToken 요청 > UserRequest 반환받음
     *                    > 반환받은 userRequest 정보로 회원 프로필을 받아야함(loadUser 메서드 역할이다!)
     * @return
     * @throws OAuth2AuthenticationException
     *
     * 해당 메서드 종료 시 @AuthenticationPrincipal이 만들어진다.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        System.out.println("userRequest.getAccessToken() = " + userRequest.getAccessToken().getTokenValue());
//        System.out.println("userRequest.getClientRegistration() = " + userRequest.getClientRegistration());
//        System.out.println("userRequest.getClientRegistration().getAuthorizationGrantType().getValue() = " + userRequest.getClientRegistration().getAuthorizationGrantType().getValue());
//        System.out.println("userRequest.getClientRegistration().getClientId() = " + userRequest.getClientRegistration().getProviderDetails());
//        System.out.println("super.loadUser(userRequest).getAttributes() = " + super.loadUser(userRequest).getAttributes());

//        강제 회원가입
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getClientId();    //google
        String providerId = oAuth2User.getAttribute("sub");
        String username = provider + "_" + providerId;
        String password = bCryptPasswordEncoder.encode("겟인데어");
        String email = oAuth2User.getAttribute("sub");
        String role = "ROLE_USER";

        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = User.builder()
                    .username(username)
                    .email(email)
                    .password(password)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(user);
        }
        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }
}
