$(document).ready(function () {
  $("#view-00037").on("click", function () {
    // PDF 파일 경로 설정
    var pdfUrl = "./file/com00037.pdf";
    window.open(pdfUrl, "_blank");
  });

  $("#view-00010").on("click", function () {
    // PDF 파일 경로 설정
    var pdfUrl = "./file/com00010.pdf";
    window.open(pdfUrl, "_blank");
  });

  $("#view-00036").on("click", function () {
    // PDF 파일 경로 설정
    var pdfUrl = "./file/com00036.pdf";
    window.open(pdfUrl, "_blank");
  });

  $("#view-30004").on("click", function () {
    // PDF 파일 경로 설정
    var pdfUrl = "./file/com30004.pdf";
    window.open(pdfUrl, "_blank");
  });

  let clickedOpenBanking = [false, false];

  $("#view-10028").on("click", function () {
    // PDF 파일 경로 설정
    var pdfUrl = "./file/com10028.pdf";
    window.open(pdfUrl, "_blank");

    clickedOpenBanking[0] = true;

    if (clickedOpenBanking.every(Boolean)) {
      // 모든 버튼이 클릭된 경우 체크박스 활성화
      $("#condition-ok").prop("disabled", false);
      $("#condition-ok-label").css("color", "black");
    }
  });

  $("#view-00027").on("click", function () {
    // PDF 파일 경로 설정
    var pdfUrl = "./file/com00027.pdf";
    window.open(pdfUrl, "_blank");

    clickedOpenBanking[1] = true;

    if (clickedOpenBanking.every(Boolean)) {
      // 모든 버튼이 클릭된 경우 체크박스 활성화
      $("#condition-ok").prop("disabled", false);
      $("#condition-ok-label").css("color", "black");
    }
  });

  $("#view-appcard").on("click", function () {
    // PDF 파일 경로 설정
    var pdfUrl = "./file/appcard_service_term_20240229.pdf";
    window.open(pdfUrl, "_blank");

    $("#condition-ok").prop("disabled", false);
    $("#condition-ok-label").css("color", "black");
  });

  // 클릭 상태를 추적하기 위한 배열
  let clickedButtons = [false, false, false, false];

  // 모든 버튼에 이벤트 리스너 추가
  $(".condition button").each(function (index) {
    $(this).on("click", function () {
      clickedButtons[index] = true; // 클릭 상태 저장

      // 모든 버튼이 클릭되었는지 체크
      if (clickedButtons.every(Boolean)) {
        // 모든 버튼이 클릭된 경우 체크박스 활성화
        $("#condition-ok").prop("disabled", false);
        $("#condition-ok-label").css("color", "black");
      }
    });
  });

  // Add 버튼 클릭 이벤트 - 이벤트 위임 사용
  $(document).on("click", ".add-input-box", function (event) {
    console.log("add");
    event.stopPropagation(); // 이벤트 버블링 방지
    event.preventDefault(); // 기본 동작 방지

    // 현재 display: none 상태인 첫 번째 요소 찾기
    var hiddenWrapper = $(".input-phone-wrapper:hidden").first();

    // 요소가 있다면 보여지도록 설정
    if (hiddenWrapper.length) {
      hiddenWrapper.css("display", "flex");
    }
  });

  // Del 버튼 클릭 이벤트 - 이벤트 위임 사용
  $(document).on("click", ".del-input-box", function (event) {
    event.stopPropagation(); // 이벤트 버블링 방지
    event.preventDefault(); // 기본 동작 방지

    // 현재 보이는 .input-phone-wrapper 요소 찾기
    var visibleWrappers = $(".input-phone-wrapper:visible");

    // 요소가 2개 이상일 때만 삭제 가능
    if (visibleWrappers.length > 1) {
      visibleWrappers.last().css("display", "none");
    }
  });

  // 금액 설정 버튼 이벤트
  $(".add-amount").click(function () {
    // 버튼에 설정된 data-amount 값을 가져오기
    var amountToAdd = parseInt($(this).data("amount"));

    // 현재 input 박스의 값 가져오기
    var currentAmount = parseInt($("#meeting-target-amount").val()) || 0; // 값이 없으면 0으로 초기화

    // 합산된 금액을 input 박스에 설정
    $("#meeting-target-amount").val(currentAmount + amountToAdd);
  });

  // 폼의 입력 이벤트 처리
  $("#join-meeting-form").on("input", function () {
    // 폼의 모든 필수 필드가 채워졌는지 확인
    var allInputsFilled =
      $("#join-meeting-form")
        .find(":input[required]")
        .filter(function () {
          return $(this).val() === "" || !this.checkValidity(); // 값이 없거나 유효하지 않은 필드를 찾음
        }).length === 0;

    // 모든 필드가 채워졌을 때 등록 버튼 활성화
    $("#btn-next").prop("disabled", !allInputsFilled); // 활성화/비활성화 설정
  });

  $("#transfer-form").on("input", function () {
    // 폼의 모든 필수 필드가 채워졌는지 확인
    var allInputsFilled =
      $("#transfer-form")
        .find(":input[required]")
        .filter(function () {
          return $(this).val() === "" || !this.checkValidity(); // 값이 없거나 유효하지 않은 필드를 찾음
        }).length === 0;

    // 모든 필드가 채워졌을 때 등록 버튼 활성화
    $("#btn-submit").prop("disabled", !allInputsFilled); // 활성화/비활성화 설정
  });

  $("#connect-card-form").on("input", function () {
    // 폼의 모든 필수 필드가 채워졌는지 확인
    var inputsFilled =
        $("#connect-card-form")
            .find(":input[required]")
            .not("#auth-check")
            .filter(function () {
              return $(this).val() === "" || !this.checkValidity(); // 값이 없거나 유효하지 않은 필드를 찾음
            }).length === 0;

    var allInputsFilled = inputsFilled && $("#auth-check").is(":checked");

    // 모든 필드가 채워졌을 때 등록 버튼 활성화
    $("#btn-next").prop("disabled", !allInputsFilled); // 활성화/비활성화 설정
  });
});
