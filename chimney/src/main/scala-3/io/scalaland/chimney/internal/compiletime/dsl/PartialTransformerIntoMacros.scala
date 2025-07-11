package io.scalaland.chimney.internal.compiletime.dsl

import io.scalaland.chimney.PartialTransformer
import io.scalaland.chimney.dsl.*
import io.scalaland.chimney.internal.compiletime.derivation.transformer.TransformerMacros
import io.scalaland.chimney.internal.compiletime.dsl.utils.DslMacroUtils
import io.scalaland.chimney.internal.runtime.{
  ArgumentLists,
  FunctionEitherToResult,
  Path,
  TransformerFlags,
  TransformerOverrides,
  WithRuntimeDataStore
}
import io.scalaland.chimney.internal.runtime.TransformerOverrides.*
import io.scalaland.chimney.partial

import scala.quoted.*

object PartialTransformerIntoMacros {

  def withFieldConstImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      T: Type,
      U: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      selector: Expr[To => T],
      value: Expr[U]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyFieldNameType { [toPath <: Path] => (_: Type[toPath]) ?=>
      '{
        WithRuntimeDataStore
          .update($ti, $value)
          .asInstanceOf[PartialTransformerInto[From, To, Const[toPath, Overrides], Flags]]
      }
    }(selector)

  def withFieldConstPartialImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      T: Type,
      U: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      selector: Expr[To => T],
      value: Expr[partial.Result[U]]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyFieldNameType { [toPath <: Path] => (_: Type[toPath]) ?=>
      '{
        WithRuntimeDataStore
          .update($ti, $value)
          .asInstanceOf[PartialTransformerInto[From, To, ConstPartial[toPath, Overrides], Flags]]
      }
    }(selector)

  def withFieldComputedImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      T: Type,
      U: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      selector: Expr[To => T],
      f: Expr[From => U]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyFieldNameType { [toPath <: Path] => (_: Type[toPath]) ?=>
      '{
        WithRuntimeDataStore
          .update($ti, $f)
          .asInstanceOf[PartialTransformerInto[From, To, Computed[toPath, Overrides], Flags]]
      }
    }(selector)

  def withFieldComputedFromImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      S: Type,
      T: Type,
      U: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      selectorFrom: Expr[From => S],
      selectorTo: Expr[To => T],
      f: Expr[S => U]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyFieldNameTypes {
      [fromPath <: Path, toPath <: Path] => (_: Type[fromPath]) ?=> (_: Type[toPath]) ?=>
        '{
          WithRuntimeDataStore
            .update($ti, $f)
            .asInstanceOf[PartialTransformerInto[From, To, ComputedFrom[fromPath, toPath, Overrides], Flags]]
        }
    }(selectorFrom, selectorTo)

  def withFieldComputedPartialImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      T: Type,
      U: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      selector: Expr[To => T],
      f: Expr[From => partial.Result[U]]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyFieldNameType { [toPath <: Path] => (_: Type[toPath]) ?=>
      '{
        WithRuntimeDataStore
          .update($ti, $f)
          .asInstanceOf[PartialTransformerInto[From, To, ComputedPartial[toPath, Overrides], Flags]]
      }
    }(selector)

  def withFieldComputedPartialFromImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      S: Type,
      T: Type,
      U: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      selectorFrom: Expr[From => S],
      selectorTo: Expr[To => T],
      f: Expr[S => partial.Result[U]]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyFieldNameTypes {
      [fromPath <: Path, toPath <: Path] => (_: Type[fromPath]) ?=> (_: Type[toPath]) ?=>
        '{
          WithRuntimeDataStore
            .update($ti, $f)
            .asInstanceOf[PartialTransformerInto[From, To, ComputedPartialFrom[fromPath, toPath, Overrides], Flags]]
        }
    }(selectorFrom, selectorTo)

  def withFieldRenamedImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      T: Type,
      U: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      selectorFrom: Expr[From => T],
      selectorTo: Expr[To => U]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyFieldNameTypes {
      [fromPath <: Path, toPath <: Path] => (_: Type[fromPath]) ?=> (_: Type[toPath]) ?=>
        '{
          $ti.asInstanceOf[
            PartialTransformerInto[From, To, RenamedFrom[fromPath, toPath, Overrides], Flags]
          ]
        }
    }(selectorFrom, selectorTo)

  def withFieldUnusedImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      T: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      selectorFrom: Expr[From => T]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyFieldNameType { [fromPath <: Path] => (_: Type[fromPath]) ?=>
      '{
        ${ ti }.asInstanceOf[PartialTransformerInto[From, To, Unused[fromPath, Overrides], Flags]]
      }
    }(selectorFrom)

  def withSealedSubtypeHandledImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      Subtype: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      f: Expr[Subtype => To]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    '{
      WithRuntimeDataStore
        .update($ti, $f)
        .asInstanceOf[PartialTransformerInto[
          From,
          To,
          CaseComputed[Path.SourceMatching[Path.Root, Subtype], Overrides],
          Flags
        ]]
    }

  def withSealedSubtypeHandledPartialImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      Subtype: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      f: Expr[Subtype => partial.Result[To]]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    '{
      WithRuntimeDataStore
        .update($ti, $f)
        .asInstanceOf[PartialTransformerInto[
          From,
          To,
          CaseComputedPartial[Path.SourceMatching[Path.Root, Subtype], Overrides],
          Flags
        ]]
    }

  def withSealedSubtypeRenamedImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      FromSubtype: Type,
      ToSubtype: Type
  ](
      td: Expr[PartialTransformerInto[From, To, Overrides, Flags]]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    '{
      $td
        .asInstanceOf[PartialTransformerInto[
          From,
          To,
          RenamedTo[Path.SourceMatching[Path.Root, FromSubtype], Path.Matching[Path.Root, ToSubtype], Overrides],
          Flags
        ]]
    }

  def withSealedSubtypeUnmatchedImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      T: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      selectorTo: Expr[To => T]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyFieldNameType { [toPath <: Path] => (_: Type[toPath]) ?=>
      '{
        ${ ti }.asInstanceOf[PartialTransformerInto[From, To, Unmatched[toPath, Overrides], Flags]]
      }
    }(selectorTo)

  def withFallbackImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      FromFallback: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      fallback: Expr[FromFallback]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    '{
      WithRuntimeDataStore
        .update($ti, $fallback)
        .asInstanceOf[PartialTransformerInto[From, To, Fallback[FromFallback, Path.Root, Overrides], Flags]]
    }

  def withFallbackFromImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      T: Type,
      FromFallback: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      selectorFrom: Expr[From => T],
      fallback: Expr[FromFallback]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyFieldNameType { [fromPath <: Path] => (_: Type[fromPath]) ?=>
      '{
        WithRuntimeDataStore
          .update($ti, $fallback)
          .asInstanceOf[PartialTransformerInto[From, To, Fallback[FromFallback, fromPath, Overrides], Flags]]
      }
    }(selectorFrom)

  def withConstructorImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      Ctor: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      f: Expr[Ctor]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyConstructorType { [args <: ArgumentLists] => (_: Type[args]) ?=>
      '{
        WithRuntimeDataStore
          .update($ti, $f)
          .asInstanceOf[PartialTransformerInto[From, To, Constructor[args, Path.Root, Overrides], Flags]]
      }
    }(f)

  def withConstructorToImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      T: Type,
      Ctor: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      selector: Expr[To => T],
      f: Expr[Ctor]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyConstructorType { [args <: ArgumentLists] => (_: Type[args]) ?=>
      DslMacroUtils().applyFieldNameType { [toPath <: Path] => (_: Type[toPath]) ?=>
        '{
          WithRuntimeDataStore
            .update($ti, $f)
            .asInstanceOf[PartialTransformerInto[From, To, Constructor[args, toPath, Overrides], Flags]]
        }
      }(selector)
    }(f)

  def withConstructorPartialImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      Ctor: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      f: Expr[Ctor]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyConstructorType { [args <: ArgumentLists] => (_: Type[args]) ?=>
      '{
        WithRuntimeDataStore
          .update($ti, $f)
          .asInstanceOf[PartialTransformerInto[
            From,
            To,
            ConstructorPartial[args, Path.Root, Overrides],
            Flags
          ]]
      }
    }(f)

  def withConstructorPartialToImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      Ctor: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      selector: Expr[To => Ctor],
      f: Expr[Ctor]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyConstructorType { [args <: ArgumentLists] => (_: Type[args]) ?=>
      DslMacroUtils().applyFieldNameType { [toPath <: Path] => (_: Type[toPath]) ?=>
        '{
          WithRuntimeDataStore
            .update($ti, $f)
            .asInstanceOf[PartialTransformerInto[
              From,
              To,
              ConstructorPartial[args, toPath, Overrides],
              Flags
            ]]
        }
      }(selector)
    }(f)

  def withConstructorEitherImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      Ctor: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      f: Expr[Ctor]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyConstructorType { [args <: ArgumentLists] => (_: Type[args]) ?=>
      '{
        WithRuntimeDataStore
          .update(
            $ti,
            FunctionEitherToResult.lift[Ctor, Any]($f)(
              ${ Expr.summon[FunctionEitherToResult[Ctor]].get }.asInstanceOf[FunctionEitherToResult.Aux[Ctor, Any]]
            )
          )
          .asInstanceOf[PartialTransformerInto[From, To, ConstructorPartial[args, Path.Root, Overrides], Flags]]
      }
    }(f)

  def withConstructorEitherToImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      T: Type,
      Ctor: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      selector: Expr[To => T],
      f: Expr[Ctor]
  )(using Quotes): Expr[PartialTransformerInto[From, To, ? <: TransformerOverrides, Flags]] =
    DslMacroUtils().applyConstructorType { [args <: ArgumentLists] => (_: Type[args]) ?=>
      DslMacroUtils().applyFieldNameType { [toPath <: Path] => (_: Type[toPath]) ?=>
        '{
          WithRuntimeDataStore
            .update(
              $ti,
              FunctionEitherToResult.lift[Ctor, Any]($f)(
                ${ Expr.summon[FunctionEitherToResult[Ctor]].get }
                  .asInstanceOf[FunctionEitherToResult.Aux[Ctor, Any]]
              )
            )
            .asInstanceOf[PartialTransformerInto[
              From,
              To,
              ConstructorPartial[args, toPath, Overrides],
              Flags
            ]]
        }
      }(selector)
    }(f)

  def withSourceFlagImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      T: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      selectorFrom: Expr[From => T]
  )(using Quotes): Expr[TransformerSourceFlagsDsl.OfPartialTransformerInto[From, To, Overrides, Flags, ? <: Path]] =
    DslMacroUtils()
      .applyFieldNameType { [fromPath <: Path] => (_: Type[fromPath]) ?=>
        '{ TransformerSourceFlagsDsl.OfPartialTransformerInto[From, To, Overrides, Flags, fromPath]($ti) }
      }(selectorFrom)

  def withTargetFlagImpl[
      From: Type,
      To: Type,
      Overrides <: TransformerOverrides: Type,
      Flags <: TransformerFlags: Type,
      T: Type
  ](
      ti: Expr[PartialTransformerInto[From, To, Overrides, Flags]],
      selectorTo: Expr[To => T]
  )(using Quotes): Expr[TransformerTargetFlagsDsl.OfPartialTransformerInto[From, To, Overrides, Flags, ? <: Path]] =
    DslMacroUtils()
      .applyFieldNameType { [toPath <: Path] => (_: Type[toPath]) ?=>
        '{ TransformerTargetFlagsDsl.OfPartialTransformerInto[From, To, Overrides, Flags, toPath]($ti) }
      }(selectorTo)
}
