// const ip = '127.0.0.1';
// const ip = 'localhost';
const ip = '172.20.10.5';

const isEmailRegistered = async (email) => {
	try {
		const response = await fetch(`http://${ip}:8080/tinglinews/users/email`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({email})
		});
		const { state, message, data } = await response.json();
		console.log(state, message, data);
		return state;
	} catch (e) {
		console.error('確認電子信箱錯誤：', e);
		Swal.fire('確認電子信箱錯誤', e, 'error');
		return false;
	}
};


// 表單提交事件處理
const handleFormSubmit = async (event) => {

	event.preventDefault();

	const email = $('#userEmail').val();
	if (!email) {
		Swal.fire('請輸入電子信箱', '', 'error');
		return;
	}
	const captcha = $('#code').val();
	if (!captcha) {
		Swal.fire('請輸入驗證碼', '', 'error');
		return;
	}
	const isCaptchaValid = await verifyCaptcha(captcha);
	if (!isCaptchaValid) {
		Swal.fire('驗證碼錯誤', '請重新輸入', 'error');
		loadCaptcha();
		return;
	}

	if (!await isEmailRegistered(email)) {
		Swal.fire('該電子信箱尚未註冊', '請先註冊 謝謝', 'error');
		setTimeout(() => {
			sessionStorage.setItem('userEmail', email);
			window.location.replace('/tinglinews/user/register.html');
		}, 2000);
		return;
	}

	const data = JSON.parse(sessionStorage.getItem('userData'));

	if (!data) {
		Swal.fire('請先登入 謝謝', '', 'warning');
		setTimeout(() => {
			sessionStorage.setItem('userEmail', email);
			window.location.replace('/tinglinews/user/login.html');
		}, 1000);
		return;
	}

	const selectedFrequency = $('input[name="frequency"]:checked').val();

	const formData = {
		userEmail: email,
		frequency: selectedFrequency,
		amount: $('#amount').val(),
		donateStatus: (selectedFrequency === '單筆') ? '已完成' : '進行中',
		userId: data.userId
	};

	sessionStorage.setItem('userEmail', email);
	await donate(formData);
};

const donate = async (formData) => {

	try {
		const response = await fetch(`http://${ip}:8080/tinglinews/users/donations`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(formData)
		});

		const { state, message, data } = await response.json();

		Swal.fire(message, '', state ? 'success' : 'warning');
		if (state) {
			setTimeout(() => {
				window.location.replace('/tinglinews/user/profile.html');
			}, 1000);
		}

	} catch (e) {
		console.error('捐款請求錯誤：', e);
		Swal.fire('捐款請求錯誤', '請稍後再試', 'error');
	}
}

const loadCaptcha = async () => {

	try {
		const response = await fetch(`http://${ip}:8080/tinglinews/users/captcha`);
		const { state, message, data } = await response.json();

		if (state) {
			$('#captcha').attr('src', `data:image/jpeg;base64,${data}`);
		} else {
			Swal.fire(message, '取得 captcha 失敗', 'error');
		}

	} catch (e) {
		console.error('取得 captcha 發生錯誤：', e);
		Swal.fire('取得 captcha 發生錯誤', '請稍後再試', 'error');
	}
};

const verifyCaptcha = async (captcha) => {
	try {
		const response = await fetch(`http://${ip}:8080/tinglinews/users/captcha/verify`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({ captcha })
		});
		const { state, message, data } = await response.json();
		// console.log(state, message, data);
		return state;
	} catch (e) {
		console.error('驗證錯誤：', e);
		return false;
	}
}

$(document).ready(() => {

	const data = JSON.parse(sessionStorage.getItem('userData'));
	$('.header-container').load(data ? '../nav-login.html' : '../nav.html');
	$('.footer-container').load('../footer.html');

	const email = sessionStorage.getItem('userEmail') || (data && data.userEmail);
	if (email) {
		$('#userEmail').val(email);
	}
	loadCaptcha();
	$('#donate-form').on('submit', handleFormSubmit);
});

