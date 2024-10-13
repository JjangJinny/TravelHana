$(document).ready(function () {
    $('#form').on('submit', function (e) {
        e.preventDefault();

        const clientId = 'ea98f95a-50de-4fbf-a1af-8f50fe34ab3c';
        const redirectUri = 'https://localhost:8080/openbanking';
        const scope = 'accounts.read transactions.read';

        const form = document.createElement('form');
        form.method = 'POST';
        form.action = 'https://auth.openbanking.com/oauth2/authorize';

        const inputClientId = document.createElement('input');
        inputClientId.type = 'hidden';
        inputClientId.name = 'client_id';
        inputClientId.value = clientId;

        const inputRedirectUri = document.createElement('input');
        inputRedirectUri.type = 'hidden';
        inputRedirectUri.name = 'redirect_uri';
        inputRedirectUri.value = redirectUri;

        const inputResponseType = document.createElement('input');
        inputResponseType.type = 'hidden';
        inputResponseType.name = 'response_type';
        inputResponseType.value = 'code';

        const inputScope = document.createElement('input');
        inputScope.type = 'hidden';
        inputScope.name = 'scope';
        inputScope.value = scope;

        form.appendChild(inputClientId);
        form.appendChild(inputRedirectUri);
        form.appendChild(inputResponseType);
        form.appendChild(inputScope);

        document.body.appendChild(form);
        form.submit();
    })
});
