package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.payload.VirtualAccountDto;

import java.util.Collection;
import java.util.List;

public interface VirtualAccountRepository {

    List<VirtualAccountDto> findAll(final String merchantId, final String status);

    int updateStatus(VirtualAccountDto virtualAccountDto, String action);

    int updatevirtualAccountDto(VirtualAccountDto virtualAccountDto);

    VirtualAccountDto getVirtualAccount(final String virtualAccountId, final String status);

    boolean existsByVirtualAccountId(final String virtualAccountId);

    int createVirtualAccount(final VirtualAccountDto virtualAccountDto);

	List<Object> findallPendingAccounts(String merchantid);
}
