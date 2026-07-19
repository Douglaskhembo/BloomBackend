package com.bloom.bloomschool.payments.repository;

import com.bloom.bloomschool.payments.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByCheckoutRequestId(String checkoutRequestId);
    Optional<PaymentTransaction> findByTransactionRef(String transactionRef);
    boolean existsByTransactionRef(String transactionRef);

    @Query("SELECT t FROM PaymentTransaction t WHERE t.status = 'UNMATCHED' ORDER BY t.receivedAt DESC")
    List<PaymentTransaction> findUnmatched();

    List<PaymentTransaction> findByStatusOrderByReceivedAtDesc(PaymentTransaction.Status status);
    List<PaymentTransaction> findByProviderOrderByReceivedAtDesc(PaymentTransaction.Provider provider);

    @Query("SELECT t FROM PaymentTransaction t ORDER BY t.receivedAt DESC")
    List<PaymentTransaction> findAllOrderByReceivedAtDesc();
}
