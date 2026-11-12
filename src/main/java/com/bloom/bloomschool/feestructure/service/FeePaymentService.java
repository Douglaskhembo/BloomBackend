package com.bloom.bloomschool.feestructure.service;


import com.bloom.bloomschool.feestructure.dto.FeePaymentRequestDto;
import com.bloom.bloomschool.feestructure.dto.FeePaymentResponseDto;
import com.bloom.bloomschool.feestructure.entity.FeePayment;
import com.bloom.bloomschool.feestructure.enumerations.FeePaymentMethod;
import com.bloom.bloomschool.feestructure.enumerations.FeeVerificationStatus;
import com.bloom.bloomschool.feestructure.repository.FeePaymentRepository;
import com.bloom.bloomschool.setup.service.ServiceGenerator;
import com.bloom.bloomschool.student.dto.StudentRequestDTO;
import com.bloom.bloomschool.student.dto.StudentSummaryDto;
import com.bloom.bloomschool.student.entity.Admissions;
import com.bloom.bloomschool.student.entity.Student;
import com.bloom.bloomschool.student.repository.AdmissionsRepository;
import com.bloom.bloomschool.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeePaymentService{
    private final FeePaymentRepository repo;
    private final ServiceGenerator serviceGenerator;
    private final StudentRepository studentRepository;
    private final AdmissionsRepository admissionsRepository;

    public List<FeePaymentResponseDto> getAll(){
        List<FeePayment> feePayments = repo.findAllByOrderByPaymentDateDesc();
        return feePayments.stream().map(this::convertToDto).toList();
    }


    public Double getBalance(UUID studentUuid){
        //get-balance(from student uuid)
        double sumConfirmedPayments = repo.sumConfirmedPayments(studentUuid);
        return sumConfirmedPayments;
    }

    private FeePaymentResponseDto convertToDto(FeePayment body){
        Student student = body.getStudent();
        StudentSummaryDto studentRequestDTO = null;
        if (student != null) {
            studentRequestDTO = StudentSummaryDto.builder()
                    .admissionNumber(student.getAdmissionNumber())
                    .fullName(student.getFirstName()+" "+student.getLastName())
                    .build();
        }

        return FeePaymentResponseDto.builder()
                .uuid(body.getUuid())
                .grade(body.getGrade())
                .stream(body.getStream())
                .account(body.getAccount())
                .expectedAmt(body.getExpectedAmt())
                .paymentMethod(body.getPaymentMethod())
                .reference(body.getReference())
                .paymentDate(body.getPaymentDate())
                .paymentSource(body.getPaymentSource())
                .verificationStatus(body.getVerificationStatus())
                .rejectReason(body.getRejectReason())
                .chequeNo(body.getChequeNo())
                .notes(body.getNotes())
                .bankName(body.getBankName())
                .student(studentRequestDTO)
                .amount(body.getAmount())
                .build();
    }

    public void create(FeePaymentRequestDto req){
        if(req.getStudentUuid() == null && req.getAdmissionUuid() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fee payment must be attached to a student");
        }
        Student student = null;
        if (req.getStudentUuid() != null) {
            student = studentRepository.findByUuid(req.getStudentUuid())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        }
        Admissions admission = null;
        if (req.getAdmissionUuid() != null) {
            admission = admissionsRepository.findByUuid(req.getAdmissionUuid())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admission not found"));
        }

        boolean isCash = req.getPaymentMethod() == FeePaymentMethod.CASH;
        String reference;
        if(isCash){
            reference = serviceGenerator.generateReference("CASH");
        }
        else {
            if(req.getReference() == null || req.getReference().isBlank()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reference is required for "
                        + req.getPaymentMethod());
            }
            reference = req.getReference();
            if(repo.existsByReference(reference)){
                throw new IllegalArgumentException("Payment reference " + reference + " already exists");
            }
        }

        FeePayment feePayment = FeePayment.builder()
                .grade(req.getGrade())
                .stream(req.getStream())
                .account(req.getAccount())
                .expectedAmt(req.getExpectedAmt() != null ? req.getExpectedAmt() : 0)
                .paymentMethod(req.getPaymentMethod())
                .reference(reference)
                .paymentDate(req.getPaymentDate() != null ? req.getPaymentDate() : LocalDateTime.now())
                .paymentSource(req.getPaymentSource())
                .verificationStatus(req.getVerificationStatus())
                .rejectReason(req.getRejectReason())
                .chequeNo(req.getChequeNo())
                .notes(req.getNotes())
                .bankName(req.getBankName())
                .student(student)
                .receiptNo(serviceGenerator.generateReference("RCPT"))
                .amount(req.getAmount())
                .admissions(admission)
                .build();

        repo.save(feePayment);
    }


    public void delete(UUID uuid){
        FeePayment existingPayment = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fee payment not found"));
        repo.delete(existingPayment);
    }

    public boolean hasConfirmedPayment(UUID uuid){
        return repo.existsByAdmissionsUuidAndVerificationStatus(uuid, FeeVerificationStatus.CONFIRMED);
    }

    public List<FeePaymentResponseDto> getPendingVerifications(UUID studentUuid){
        List<FeePayment> feePayments = repo.getPendingFeePayments(studentUuid);
        return feePayments.stream().map(this::convertToDto).toList();
    }

    public void verifyPayment(UUID uuid){
        FeePayment existingPayment = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fee payment not found"));
        if (existingPayment.getVerificationStatus() != FeeVerificationStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Payment already processed");
        }
        existingPayment.setVerificationStatus(FeeVerificationStatus.CONFIRMED);
        repo.save(existingPayment);
    }

    public void rejectPayment(UUID uuid){
        FeePayment existingPayment = repo.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fee payment not found"));
        if (existingPayment.getVerificationStatus() != FeeVerificationStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Payment already processed");
        }
        existingPayment.setVerificationStatus(FeeVerificationStatus.REJECTED);
        repo.save(existingPayment);
    }
}
