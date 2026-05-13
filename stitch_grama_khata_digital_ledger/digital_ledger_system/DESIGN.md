---
name: Digital Ledger System
colors:
  surface: '#fcf8ff'
  surface-dim: '#dcd8e3'
  surface-bright: '#fcf8ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f6f2fc'
  surface-container: '#f0ecf6'
  surface-container-high: '#eae6f1'
  surface-container-highest: '#e4e1eb'
  on-surface: '#1b1b22'
  on-surface-variant: '#464553'
  inverse-surface: '#303037'
  inverse-on-surface: '#f3eff9'
  outline: '#777584'
  outline-variant: '#c8c4d5'
  surface-tint: '#544fc0'
  primary: '#1f108e'
  on-primary: '#ffffff'
  primary-container: '#3730a3'
  on-primary-container: '#a9a7ff'
  inverse-primary: '#c3c0ff'
  secondary: '#4648d4'
  on-secondary: '#ffffff'
  secondary-container: '#6063ee'
  on-secondary-container: '#fffbff'
  tertiary: '#511c00'
  on-tertiary: '#ffffff'
  tertiary-container: '#752c00'
  on-tertiary-container: '#fe9562'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#e2dfff'
  primary-fixed-dim: '#c3c0ff'
  on-primary-fixed: '#0f0069'
  on-primary-fixed-variant: '#3b35a7'
  secondary-fixed: '#e1e0ff'
  secondary-fixed-dim: '#c0c1ff'
  on-secondary-fixed: '#07006c'
  on-secondary-fixed-variant: '#2f2ebe'
  tertiary-fixed: '#ffdbcc'
  tertiary-fixed-dim: '#ffb694'
  on-tertiary-fixed: '#351000'
  on-tertiary-fixed-variant: '#7a3003'
  background: '#fcf8ff'
  on-background: '#1b1b22'
  surface-variant: '#e4e1eb'
typography:
  display:
    fontFamily: Public Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Public Sans
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-md:
    fontFamily: Public Sans
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Public Sans
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Public Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-lg:
    fontFamily: Public Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-sm:
    fontFamily: Public Sans
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 8px
  xs: 4px
  sm: 12px
  md: 16px
  lg: 24px
  xl: 32px
  gutter: 16px
  margin-mobile: 20px
---

## Brand & Style

This design system is built on the pillars of **Trust, Clarity, and Modernity**. It aims to bridge the gap between high-end fintech aesthetics and the functional needs of diverse user bases, including those in rural settings. The personality is professional yet approachable, utilizing a "Modern Corporate" aesthetic that favors breathability over density.

The visual style eliminates the visual noise of traditional tabular ledgers—removing harsh lines and heavy borders—and replaces them with soft shadows and intentional whitespace. This creates a focused environment where financial data feels less overwhelming and more manageable.

## Colors

The palette is anchored by **Deep Indigo**, a color associated with stability and institutional trust. This is used for primary actions and key brand touchpoints. 

The background utilizes **Soft Neutrals** (Cool Slates) to reduce eye strain and provide a premium, clean canvas. Semantic colors are dialed back to more sophisticated, "soft" versions of their standard counterparts:
- **Settled (Success):** An emerald green that signals growth and resolution without being jarring.
- **Dues (Error/Warning):** A soft coral/peach-red that highlights urgency while maintaining the "friendly" atmosphere requested.
- **Surface Colors:** Pure white is used sparingly for cards and interactive containers to make them "pop" against the soft neutral background.

## Typography

**Public Sans** is chosen for its exceptional legibility and institutional neutrality. It performs well at both large sizes for balance displays and very small sizes for transaction timestamps.

The hierarchy is strictly enforced to aid rural users who may be less familiar with complex UI patterns. We use high-contrast font weights (600 and 700) for headers to ensure the primary information—such as the amount owed or a person's name—is immediately visible. Body text is kept at a comfortable 16px minimum for accessibility.

## Layout & Spacing

This design system employs a **Fluid Grid** model with a focus on generous vertical rhythm. 

- **Mobile:** A single-column layout with 20px side margins to prevent accidental taps near the bezel.
- **Padding:** We use a "loose" spacing model. Cards and containers have internal padding of at least 16px (md) or 24px (lg) to ensure information is not cramped.
- **Rhythm:** Elements are spaced in multiples of 8px. Transaction items in a list should have at least 12px of vertical separation to distinguish them as individual units without needing heavy divider lines.

## Elevation & Depth

To maintain a premium aesthetic without heavy borders, depth is created through **Ambient Shadows**. 

We utilize three tiers of elevation:
1. **Base (0):** The soft neutral background.
2. **Surface (Low):** Standard cards and list items use a subtle, diffused shadow (Blur: 10px, Y: 2px, Opacity: 4% Black) to lift them slightly off the background.
3. **Overlay (High):** Modals, bottom sheets, and primary action buttons use a more pronounced shadow (Blur: 20px, Y: 8px, Opacity: 8% Indigo) to indicate high interactivity and focus.

Backdrop blurs are used sparingly on sticky headers to maintain context as users scroll through long ledger histories.

## Shapes

The shape language is defined by **Rounded (Level 2)** corners. This increased radius softens the "hard" nature of financial data, making the app feel more like a helpful assistant and less like a formal accounting tool.

- **Buttons & Cards:** 0.5rem (8px) base radius.
- **Input Fields:** 0.5rem (8px) for a modern, consistent feel.
- **Selection Indicators:** Pill shapes are used for chips and status tags (Settled/Due) to create a distinct visual contrast with the rectangular cards they sit upon.

## Components

### Buttons
Primary buttons use the Deep Indigo fill with white text. They should have a minimum height of 48px to remain accessible for all users. Secondary buttons use a subtle indigo tint background with indigo text, rather than an outline.

### Cards (The Ledger Entry)
The core component of this design system. Each card features a person's name in bold, the latest transaction date in a secondary label style, and the balance in a large, right-aligned headline. No borders; only the "Low" elevation shadow.

### Input Fields
Inputs are background-filled with a slightly darker neutral than the page background. They use a 2px indigo "focus ring" rather than a border to signal activity.

### Status Chips
Small, pill-shaped indicators for "Settled" or "Due". These use a low-saturation background of the semantic colors (Emerald/Coral) with high-saturation text to ensure high readability and a "premium" muted look.

### Balance Summary
A high-impact component at the top of the screen. It uses a gradient of Deep Indigo to Secondary Indigo with white typography to create a clear "Hero" area that summarizes the user's total financial standing.