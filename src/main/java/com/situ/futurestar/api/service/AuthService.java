package com.situ.futurestar.api.service;

import com.situ.futurestar.core.common.Result;
import com.situ.futurestar.core.dto.*;
import com.situ.futurestar.core.entity.PhysicalRecord;
import com.situ.futurestar.core.vo.LoginVO;
import com.situ.futurestar.core.vo.PageResult;
import com.situ.futurestar.core.vo.TokenVO;


public interface AuthService {
    Result<Void> sendCode(SendCodeDTO sendCodeDTO);

     Result<Void> register(RegisterDTO registerDTO);

    Result<LoginVO> login(LoginDTO loginDTO);

    Result<TokenVO> refresh(String refreshToken);

    void logout(String token);

    void resetPassword(ResetPasswordDTO resetPasswordDTO);


}
