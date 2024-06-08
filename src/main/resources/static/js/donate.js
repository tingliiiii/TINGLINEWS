// 表單提交事件處理
const handleFormSubmit = async (event) => {

    event.preventDefault();

    const formData = {
        userEmail: $('#userEmail').val(),
        frequency: $('#frequency').val(),
        amount: $('#amount').val(),
        donateStatus: ($('#frequency').val() == '單筆') ? '已完成' : '進行中',
        end_time: ($('#frequency').val() == '單筆') ? '' : new Date().toString(),
        userId: sessionStorage.getItem('userId')
    };
    await donate(formData);
};

const donate = async (formData) => {

    const id = sessionStorage.getItem('userId');

    if (id == null) {
        sessionStorage.setItem('userEmail', formData.userEmail);
        Swal.fire('請先登入 謝謝', '', 'warning');
        setTimeout(() => {
            window.location.replace('/tinglinews/user/login.html');
        }, 1000);
        return;
    }


    try {
        const response = await fetch('http://localhost:8080/tinglinews/user/donate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        const { state, message, data } = await response.json(); // 等待回應本文內容
        // console.log(state, message, data);

        if (state) {
            Swal.fire(message, '', 'success');
            setTimeout(() => {
                window.location.replace('/tinglinews/user/profile.html');
            }, 1000);
        } else {
            Swal.fire(message, '', 'warning');
        }

    } catch (e) {
        console.error('捐款請求錯誤：', e);
    }
}

const captcha = async () => {

    const captcha = $('#captcha');

    try {
        const response = await fetch('http://localhost:8080/tinglinews/user/captcha');
        const { state, message, data } = await response.json();

        if (state) {
            captcha.attr('src', 'data:image/jpeg;base64,' + data);
        } else {
            Swal.fire(message, '取得 captcha 失敗', 'error');
            window.location.reload();
        }
    } catch (e) {
        console.error('取得 captcha 發生錯誤：', e);
    }
};

$(document).ready(() => {

    if (sessionStorage.getItem('userId') != null) {
        $('.header-container').load('../nav-login.html');
    } else {
        $('.header-container').load('../nav.html');
    }
    $('#online-tab').addClass('active');
    $('.footer-container').load('../footer.html');

    const email = sessionStorage.getItem('userEmail');
    $('#userEmail').val(email);

    captcha();

    $('#donate-form').on('submit', handleFormSubmit);
});

