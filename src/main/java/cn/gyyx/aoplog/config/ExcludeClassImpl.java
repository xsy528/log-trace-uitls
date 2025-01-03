package cn.gyyx.aoplog.config;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class ExcludeClassImpl implements ExcludeClass{

    @Override
    public boolean isExcluded(Object o) {
        if(o == null){
            return true;
        }else if (o instanceof HttpServletResponse) {
            return true;
        } else if (o instanceof BindingResult) {
            return true;
        } else if (o instanceof HttpServletRequest) {
            return true;
        } else if (o instanceof MultipartFile) {
            return true;
        }
        return false;
    }
}
