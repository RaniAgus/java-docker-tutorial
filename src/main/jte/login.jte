@param io.github.raniagus.example.views.LoginView view

@template.base(title = "Login", body = @`
    <main class="container">
        <h1>Iniciar sesión</h1>

        <form action="/login" method="post">
            <label for="email">Email</label>
            <input type="email" name="email" id="email" placeholder="Email" value="${view.email()}" required autofocus>

            <label for="password">Contraseña</label>
            <input type="password" name="password" id="password" placeholder="Contraseña" required>

            <input type="hidden" name="origin" value="${view.origin()}">

            <button type="submit">Login</button>
        </form>

        @if("NOT_FOUND".equals(view.error()))
            <p>Usuario o contraseña incorrectos.</p>
        @elseif(view.error() != null)
            <p>Ocurrió un error. Volvé a intentar en otro momento.</p>
        @endif
    </main>
`)
