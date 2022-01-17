package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.enums.VirtualAccountStatusEnum;
import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.VirtualAccountDto;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.repository.VirtualAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class VirtualAccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VirtualAccountRepository virtualAccountRepository;

    public List<VirtualAccountDto> findAll(String status) {
        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            throw new GenericCustomException("Error: Invalid session", new Date());
        }

        return virtualAccountRepository.findAll(user.getMerchantId(), status);

    }

    public int updateByVirtualaccountId(VirtualAccountDto virtualAccountDto, String action) {
        int virtualClouser = virtualAccountRepository.updateStatus(virtualAccountDto,action);
        return virtualClouser;
    }

    public int virtualAccountDtos(VirtualAccountDto virtualAccountDto) {
        int virtualClouser = virtualAccountRepository.updatevirtualAccountDto(virtualAccountDto);
        return virtualClouser;
    }

    public VirtualAccountDto getVirtualAccount(final String virtualAccountId, final String status) {
        return virtualAccountRepository.getVirtualAccount(virtualAccountId, status);
    }

    public VirtualAccountDto getVirtualAccount(final String virtualAccountId) {
        return getVirtualAccount(virtualAccountId, VirtualAccountStatusEnum.ACTIVE.name());
    }

    public boolean existsByVirtualAccountId(final String virtualAccountId) {
        return virtualAccountRepository.existsByVirtualAccountId(virtualAccountId);
    }

    public void createVirtualAccount(final VirtualAccountDto virtualAccountDto) {
        virtualAccountRepository.createVirtualAccount(virtualAccountDto);
    }

	public List<Object> findAllPendingAccounts(String merchantid) {
		List<Object> virtualAccountDto = virtualAccountRepository.findallPendingAccounts(merchantid);
		return virtualAccountDto;
	}
}
