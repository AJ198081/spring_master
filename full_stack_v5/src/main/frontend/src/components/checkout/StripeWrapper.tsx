import type {ReactNode} from 'react';
import { Elements } from '@stripe/react-stripe-js';
import { loadStripe } from '@stripe/stripe-js';

// Initialize Stripe with the publishable key
const stripePromise = loadStripe('pk_test_51RrfbiRuOLFZ6IEdZcgohwXX4p8LXcSnHcmt7Qut8qpbDq6Sc0oQyRRc2DEgmN2AkgggJZSp0YfyOwq8oTvagKZq00TYRkOhoW');

interface StripeWrapperProps {
  children: ReactNode;
}

export const StripeWrapper = ({ children }: StripeWrapperProps) => {
  return (
    <Elements stripe={stripePromise}>
      {children}
    </Elements>
  );
};