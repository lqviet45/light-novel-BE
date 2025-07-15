package com.lqviet.accountservices.repositories;

import com.lqviet.accountservices.entities.Account;
import com.lqviet.baseentity.repository.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Account Repository extending BaseRepository from your custom library
 * Inherits 20+ methods from BaseRepository including:
 * <pre>
 * - findByIdNotDeleted(), findAllActive(), findAllDeleted()
 * - softDeleteById(), restoreById(), permanentlyDeleteOldRecords()
 * - findByCreatedAtBetween(), findRecentlyCreated(), findRecentlyUpdated()
 * - countActive(), countDeleted(), existsByIdNotDeleted()
 * - And many more...
 * </pre>
 */
@Repository
public interface AccountRepository extends BaseRepository<Account> {
}
