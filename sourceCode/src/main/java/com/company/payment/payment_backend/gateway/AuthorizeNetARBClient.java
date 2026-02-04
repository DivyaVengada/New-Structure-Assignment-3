package com.company.payment.payment_backend.gateway;

import com.company.payment.payment_backend.model.dto.AuthorizeNetProperties;
import net.authorize.Environment;
import net.authorize.api.contract.v1.*;
import net.authorize.api.controller.ARBCreateSubscriptionController;
import net.authorize.api.controller.base.ApiOperationBase;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.GregorianCalendar;

@Component
public class AuthorizeNetARBClient {

    private  final AuthorizeNetProperties properties;

    public AuthorizeNetARBClient(AuthorizeNetProperties properties) {
        this.properties = properties;
    }

    private void init() {
        Environment env = "PRODUCTION".equalsIgnoreCase(properties.getEnvironment())
                ? Environment.PRODUCTION
                : Environment.SANDBOX;

        ApiOperationBase.setEnvironment(env);

        MerchantAuthenticationType merchantAuthentication = new MerchantAuthenticationType();
        merchantAuthentication.setName(properties.getApiLoginId());
        merchantAuthentication.setTransactionKey(properties.getTransactionKey());
        ApiOperationBase.setMerchantAuthentication(merchantAuthentication);
    }

    public String createMonthlySubscription(Double amount) {
        init();

        PaymentScheduleType.Interval interval = new PaymentScheduleType.Interval();
        interval.setLength((short) 1); // every 1 month
        interval.setUnit(ARBSubscriptionUnitEnum.MONTHS);

        PaymentScheduleType schedule = new PaymentScheduleType();
        schedule.setInterval(interval);
        schedule.setStartDate(toXmlDate(LocalDate.now()));
        schedule.setTotalOccurrences((short) 9999); // effectively "until cancelled"

        // NOTE: for production, do NOT hardcode card details.
        CreditCardType card = new CreditCardType();
        card.setCardNumber("4111111111111111");
        card.setExpirationDate("2025-12");

        PaymentType payment = new PaymentType();
        payment.setCreditCard(card);

        ARBSubscriptionType subscription = new ARBSubscriptionType();
        subscription.setName("Monthly plan");
        subscription.setAmount(BigDecimal.valueOf(amount));
        subscription.setPaymentSchedule(schedule);
        subscription.setPayment(payment);

        ARBCreateSubscriptionRequest request = new ARBCreateSubscriptionRequest();
        request.setSubscription(subscription);

        ARBCreateSubscriptionController controller = new ARBCreateSubscriptionController(request);
        controller.execute();

        ARBCreateSubscriptionResponse response = controller.getApiResponse();

        if (response == null ||
                response.getMessages() == null ||
                response.getMessages().getResultCode() != MessageTypeEnum.OK) {
            String message = (response != null && response.getMessages() != null &&
                    !response.getMessages().getMessage().isEmpty())
                    ? response.getMessages().getMessage().get(0).getText()
                    : "Unknown error creating ARB subscription";
            throw new RuntimeException("Authorize.Net ARB error: " + message);
        }

        return response.getSubscriptionId();
    }

    private XMLGregorianCalendar toXmlDate(LocalDate date) {
        try {
            GregorianCalendar gc = GregorianCalendar.from(date.atStartOfDay(ZoneId.systemDefault()));
            return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(
                    gc.get(GregorianCalendar.YEAR),
                    gc.get(GregorianCalendar.MONTH) + 1, // Java months are 0‑based
                    gc.get(GregorianCalendar.DAY_OF_MONTH),
                    DatatypeConstants.FIELD_UNDEFINED
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert LocalDate to XMLGregorianCalendar", e);
        }
    }
}