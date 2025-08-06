# Using Environment Variables in Vite

This document explains how to use environment variables in Vite applications.

## Environment Variables in Vite

Vite exposes environment variables on the special `import.meta.env` object. By default, the following environment variables are available:

- `import.meta.env.MODE`: The mode the app is running in (e.g., 'development', 'production')
- `import.meta.env.BASE_URL`: The base URL the app is being served from
- `import.meta.env.PROD`: Whether the app is running in production
- `import.meta.env.DEV`: Whether the app is running in development

## Custom Environment Variables

To define custom environment variables, create a `.env` file in the root of your project. Variables in this file must be prefixed with `VITE_` to be exposed to your Vite-processed code.

Example `.env` file:
```
VITE_API_URL=https://api.example.com
VITE_STRIPE_PUBLIC_KEY=pk_test_your_key_here
```

## Accessing Environment Variables

You can access these variables in your code using `import.meta.env.VITE_VARIABLE_NAME`:

```typescript
// Example: Using the Stripe publishable key from environment variables
import { loadStripe } from '@stripe/stripe-js';
const stripePromise = loadStripe(import.meta.env.VITE_STRIPE_PUBLIC_KEY);
```

## Environment-Specific Variables

You can also create environment-specific `.env` files:

- `.env.development` - Used in development mode
- `.env.production` - Used in production mode
- `.env.local` - Loaded in all cases, overrides other files

## Type Definitions (TypeScript)

For TypeScript support, you may need to add type definitions for your custom environment variables. Create or update the `env.d.ts` file in your `src` directory:

```typescript
/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_STRIPE_PUBLIC_KEY: string;
  // Add other environment variables here
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
```

## Security Considerations

- Never expose sensitive information (like API secrets) in your frontend code
- Only use public keys and tokens that are meant to be exposed to the client
- Keep your `.env` files out of version control (add them to `.gitignore`)