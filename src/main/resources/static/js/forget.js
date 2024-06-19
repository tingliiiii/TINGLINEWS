// const ip = '127.0.0.1';
// const ip = 'localhost';
const ip = '172.20.10.5';

const fetchData = async (url, method, body) => {
	try {
		const response = await fetch(url, {
			method: method,
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(body)
		});
		return await response.json();
	} catch (e) {
		console.error('請求錯誤：', e);
		throw e;
	}
}

const sendEmail = async () => {

	const email = $('#userEmail').val();

	if (!email) {
		Swal.fire('請輸入電子信箱', '', 'error');
		return;
	}

	try {
		const { state, message, data } = await fetchData(`http://${ip}:8080/tinglinews/users/totp`, 'POST', { 'toEmail': email });
		console.log(state, message, data);

		if (state !== true) {
			Swal.fire(message, data, 'error');
			return;
		}

		Swal.fire(message, '', 'success');
		$('#userEmail').prop('disabled', true);
		$('#verification-section').show();

	} catch (e) {
		console.error('傳送驗證碼錯誤：', e);
		Swal.fire('傳送驗證碼錯誤 請稍後再試', e, 'error');
	}
}

const verifyOTP = async () => {
	const totp = $('#totp').val();
	try {
		const { state, message, data } = await fetchData(`http://${ip}:8080/tinglinews/users/totp/verify`, 'POST', { 'totp': totp });
		console.log(state, message, data);
		if (state !== true) {
			Swal.fire(message, '', 'error');
			return;
		}
		Swal.fire(message, '', 'success');
		$('#reset-section').show();
		// 增加 required 屬性
		// $('#userPassword').prop('required', true);
		// $('#passwordConfirm').prop('required', true);

	} catch (e) {
		console.error('驗證錯誤：', e);
		Swal.fire('驗證錯誤 請稍後再試', e, 'error');
	}
}

const handleSubmit = async (event) => {
	event.preventDefault();

	const email = $('#userEmail').val();
	const password = $('#userPassword').val();

	if (password !== $('#passwordConfirm').val()) {
		Swal.fire('密碼不一致', '', 'error');
		return;
	}

	try {
		const { state, message, data } = await fetchData(`http://${ip}:8080/tinglinews/users/password`, 'PATCH', { "email": email, "password": password });
		console.log(state, message, data);
		if (state !== true) {
			Swal.fire(message, '', 'error');
			return;
		}
		Swal.fire(message, '', 'success');
		setTimeout(() => {
			window.location.replace('/tinglinews/user/login.html');
		}, 1000);
	} catch (e) {
		console.error('重設密碼錯誤：', e);
		Swal.fire('重設密碼錯誤 請稍後再試', e, 'error');
	}
}

$(document).ready(() => {

	$('.header-container').load('../nav.html');
	$('.footer-container').load('../footer.html');

	const email = sessionStorage.getItem('userEmail');
	$('#userEmail').val(email);

	$('#send-email').on('click', sendEmail);
	$('#verify').on('click', verifyOTP);
	$('#forget-form').on('submit', handleSubmit);

	// 輸入 email 時按下 enter 鍵，觸發傳送驗證碼按鈕
	$('#userEmail').on('keypress', (event) => {
		if (event.which === 13) {
			event.preventDefault();
			$('#send-email').click();
		}
	});

	// 輸入 otp 時按下 enter 鍵，觸發驗證按鈕
	$('#otp').on('keypress', (event) => {
		if (event.which === 13) {
			event.preventDefault();
			$('#verify').click();
		}
	});

	// 輸入密碼時按下 enter 鍵，觸發重設密碼按鈕
	$('#passwordConfirm').on('keypress', (event) => {
		if (event.which === 13) {
			event.preventDefault();
			$('#forget-form').submit();
		}
	});
});
