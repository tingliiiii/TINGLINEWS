const ip = '172.20.10.5';

const handleSubmit = async (event) => {
    event.preventDefault();

    const email = $('#userEmail').val();
    const password = $('#userPassword').val();

    if (password !== $('#passwordConfirm').val()) {
        Swal.fire('密碼不一致', '', 'error');
        return;
    }

    try {
        const response = await fetch(`http://${ip}:8080/tinglinews/users/password`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });

        const { state, message, data } = await response.json();
        console.log(state, message, data);
        if (state !== true) {
            Swal.fire(message, '', 'error');
            setTimeout(() => {
                window.location.replace('/tinglinews/user/forgot.html');
            }, 1000);
            return;
        }

        Swal.fire(message, '', 'success');
        setTimeout(() => {
            sessionStorage.removeItem('userEmail');
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

    $('#reset-form').on('submit', handleSubmit);

    $('#passwordConfirm').on('keypress', (event) => {
        if (event.which === 13) {
            event.preventDefault();
            $('#reset-form').submit();
        }
    });

});
