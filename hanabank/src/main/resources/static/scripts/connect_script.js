$(document).ready(function () {
    function checkRequiredFields() {
        const customerId = $('#consumerName').val().trim();
        const ssnPart1 = $('.input-container input[type="tel"]').val().trim();
        const ssnPart2 = $('.input-container input[type="password"]').val().trim();
        const telco = $('.sel-tele').val();
        const phoneMiddle = $('#phone-middle').val().trim();
        const phoneLast = $('#phone-last').val().trim();

        // 모든 필드가 채워졌는지 확인
        if (customerId && ssnPart1 && ssnPart2 && telco !== '통신사' && phoneMiddle && phoneLast) {
            $('#btn-connect-confirm').prop('disabled', false); // 버튼 활성화
        } else {
            $('#btn-connect-confirm').prop('disabled', true); // 버튼 비활성화
        }
    }

    // 모든 필드에 이벤트 리스너 추가
    $('#consumerName, .input-container input[type="tel"], .input-container input[type="password"], .sel-tele, #phone-middle, #phone-last').on('input change', checkRequiredFields);

    $("#btn-connect-confirm").on("click", function() {
        const phone = $("#phone-first").val() + $("#phone-middle").val() + $("#phone-last").val();

        $.ajax({
            url: '/phoneAuthentication',
            method: 'POST',
            data: {to: phone},
            success: function (response) {
                console.log("success")
                $(".authentication-api").css("display", "flex");
                $("#auth-btn").on("click", function () {
                    if ($("#auth-num").val() === response) {
                        $("#auth-check").prop("checked", true);
                        $("#auth-check").prop("disable", true);
                        $(this).text("완료");
                        $("#connect-card-form").trigger("input");
                    } else {
                        swal("인증번호가 일치하지 않습니다.", "", "error")
                    }
                })

            },
            error: function (xhr, status, error) {
                console.error('Error phone authentication:', error);
            }
        });
    });

    $('#connect-card-form').on('submit', function (e) {
        e.preventDefault();

        $('#consumerNum').val($('#consumerNumFront').val() + $('#consumerNumBack').val());
        $('#consumerPnum').val($("#phone-first").val() + $("#phone-middle").val() + $("#phone-last").val());

        $(this).off('submit').submit();
    });
});