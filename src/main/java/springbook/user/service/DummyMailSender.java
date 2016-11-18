package springbook.user.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class DummyMailSender implements MailSender {

	@Override
	public void send(SimpleMailMessage arg0) throws MailException {
		System.out.println("[CALL1]" + this.getClass().getName() + '@' + Thread.currentThread().getStackTrace()[1].getMethodName());
		System.out.println("[CALL1]" + arg0.toString());
	}

	@Override
	public void send(SimpleMailMessage[] arg0) throws MailException {
		System.out.println("[CALL2]" + this.getClass().getName() + '@' + Thread.currentThread().getStackTrace()[1].getMethodName());
		System.out.println("[CALL1]" + arg0.toString());
	}

}
