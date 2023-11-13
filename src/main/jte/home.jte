@param io.github.raniagus.example.views.HomeView view

@template.base(
title = "Home",
body = @`
    <main class="container">
        <section x-data="{ count: 0 }">
            <h1>¡Hola, ${view.nombre()} ${view.apellido()}! 👋🏻</h1>
            <p>
                Contar ovejas es un ejercicio mental muy usado para ayudarse a uno mismo a dormir.
                ¿Funciona? Ni idea, intentémoslo.
            </p>
            <h2 class="counter"><span x-text="count"></span> ovejas</h2>
            <form class="grid" method="post" action="/logout">
                <button @click="$event.preventDefault(); count++">Contar</button>
                <button type="submit" class="secondary">Cerrar sesión</button>
            </form>
        </section>
    </main>
`)
